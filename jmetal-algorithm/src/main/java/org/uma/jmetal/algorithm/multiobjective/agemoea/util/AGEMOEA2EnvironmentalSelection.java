package org.uma.jmetal.algorithm.multiobjective.agemoea.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.stream;

public class AGEMOEA2EnvironmentalSelection<S extends Solution<?>> {
    private static String attributeId = AGEMOEA2EnvironmentalSelection.class.getName();
    private double P;
    private List<Double> intercepts;
    private int numberOfObjectives;


    public AGEMOEA2EnvironmentalSelection(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
    }

    public List execute(List<S> solutionList, int solutionsToSelect) throws JMetalException {
        Ranking<S> ranking = new FastNonDominatedSortRanking<>() ;
        ranking.compute(solutionList) ;
        return survivalScore(ranking, solutionsToSelect);
    }

    protected List<S> survivalScore(Ranking<S> ranking, int solutionsToSelect) {
        List<S> population = new ArrayList<>(solutionsToSelect) ;

        this.computeSurvivalScore(ranking.getSubFront(0));
        if (ranking.getSubFront(0).size() <= solutionsToSelect){
            population.addAll(ranking.getSubFront(0));
        } else {
            ranking.getSubFront(0).sort(new SurvivalScoreComparator());
            population.addAll(ranking.getSubFront(0).subList(0, solutionsToSelect));
        }

        int rankingIndex = 1;
        while (population.size() < solutionsToSelect) {
            this.assignConvergenceScore(ranking.getSubFront(rankingIndex));
            if ((population.size() + ranking.getSubFront(rankingIndex).size()) <= solutionsToSelect) {
                population.addAll(ranking.getSubFront(rankingIndex));
            } else {
                ranking.getSubFront(rankingIndex).sort(new SurvivalScoreComparator());
                int nSolutions = solutionsToSelect - population.size();
                population.addAll(ranking.getSubFront(rankingIndex).subList(0, nSolutions));
            }
            rankingIndex++;
        }

        return population ;
    }

    public void computeSurvivalScore(List<S> front){
        // list to keep track of solutions selected when assigning the survival scores
        List<Integer> selected = new ArrayList<>();

        // compute ideal point
        List<Double> ideal_point = computeIdealPoint(front);

        // find extreme points
        List<S> extreme_points = this.findExtremes(front);

        // set crowding distance for
        for (S extreme : extreme_points){
            for (int index = 0; index<front.size(); index++){
                if (extreme == front.get(index)){
                    selected.add(index);
                    extreme.attributes().put(attributeId, Double.POSITIVE_INFINITY);
                }
            }
        }

        if (front.size() <= this.numberOfObjectives){
            intercepts = new ArrayList<>();
            for(int i=0; i<this.numberOfObjectives; i++){
                double value = 0.0;
                for (S s : front){
                    if (s.objectives()[i] > value)
                        value = s.objectives()[i];
                }
                intercepts.add(value);
            }
            return;
        }


        // normalize the front
        List<double[]> normalizedFront = new ArrayList<>();

        intercepts = constructHyperplane(front, extreme_points);

        for (int i=0; i<front.size(); i++){
            double[] objectives = front.get(i).objectives().clone();
            for (int j=0; j<numberOfObjectives; j++){
                objectives[j] = (objectives[j] - ideal_point.get(j)) / (intercepts.get(j) - ideal_point.get(j));
            }
            normalizedFront.add(i, objectives);
        }

        // compute geometry
        this.P = computeGeometry(normalizedFront, selected);

        // Proximity Score
        double[] nn = new double[normalizedFront.size()];
        double[] utopia = new double[numberOfObjectives];
        for (int i=0; i<nn.length; i++){
            nn[i] = minkowskiDistance(normalizedFront.get(i), utopia, P);
        }

        // Survival Score
        double[][] distances = new double[normalizedFront.size()][normalizedFront.size()];
        for (int i=0; i<normalizedFront.size()-1; i++){
            for (int j=i; j<normalizedFront.size(); j++){
                distances[i][j] = this.minkowskiDistance(normalizedFront.get(i), normalizedFront.get(j), P);
                distances[j][i] = distances[i][j];

                distances[i][j] /= nn[i];
                distances[j][i] /= nn[j];
            }
        }

        List<Integer> remaining = IntStream.range(0, front.size()).boxed().collect(Collectors.toList());
        remaining.removeAll(selected);
        while (remaining.size() > 0){
            double[] values = this.findMoreDiverseSolution(distances, selected, remaining);
            int index = (int) values[0];
            remaining.remove((Integer) index);
            selected.add(index);
            front.get(index).attributes().put(attributeId, values[1]);
        }
    }

    protected double computeGeometry(List<double[]> normalizedFront, List<Integer> extremePoints) {
        // nadir point
        double[] nadir = new double[this.numberOfObjectives];
        double[] utopia = new double[this.numberOfObjectives];

        for (int i=0; i<this.numberOfObjectives; i++){
            nadir[i] = 1;
        }

        // find central point
        double[] d = new double[normalizedFront.size()] ;
        for (int i=0; i<d.length; i++){
            if (extremePoints.contains(i)) {
                d[i] = Double.POSITIVE_INFINITY;
            } else {
                d[i] = this.point2LineDistance(normalizedFront.get(i), utopia, nadir);
            }
        }

        double[] centralPoint = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (int i=0; i<d.length; i++){
            if (d[i] < minDistance){
                minDistance = d[i];
                centralPoint = normalizedFront.get(i);
            }
        }
        assert(centralPoint != null);
        double average = 0;
        try {
            average = stream(centralPoint).sum() / numberOfObjectives;
        } catch(Exception e){
            System.out.println(centralPoint);
        }
        double p = Math.log(this.numberOfObjectives) / Math.log(1.0 / average) ;
        //System.out.println(p);

        if (Double.isNaN(p) || p <0.10 || Double.isInfinite(p))
            p = 1;

        return p;
    }

    protected void assignConvergenceScore(List<S> front){
        double[] idealPoint = new double[this.numberOfObjectives];
        for (S solution : front){
            double[] normalizedObjective = solution.objectives().clone();
            for(int i=0; i<this.numberOfObjectives; i++){
                normalizedObjective[i] = normalizedObjective[i] / intercepts.get(i);
            }
            double value =  this.minkowskiDistance(normalizedObjective, idealPoint, P);
            solution.attributes().put(attributeId, value);
        }
    }

    protected List<S> findExtremes(List<S> front) {
        List<S> extremePoints = new ArrayList<>();
        for (int i=0; i<this.numberOfObjectives; i++){
            double[] axes = new double[this.numberOfObjectives];
            axes[i] = 1;

            double minDistance = Double.POSITIVE_INFINITY;
            int minIndex = -1;
            for (int j=0; j<front.size(); j++){
                double dist = this.point2LineDistance(front.get(j).objectives(), new double[this.numberOfObjectives], axes);
                if (dist < minDistance){
                    minDistance = dist;
                    minIndex = j;
                }
            }
            extremePoints.add(front.get(minIndex));
        }
        return extremePoints;
    }

    private double[] findMoreDiverseSolution(double[][] distances, List<Integer> selected, List<Integer> remaining) {
        double bestValue = 0;
        int bestIndex = -1;

        for (int index1 : remaining){
            double minValue1 = Double.POSITIVE_INFINITY;
            double minValue2 = Double.POSITIVE_INFINITY;

            for (int index2 : selected){
                if (distances[index1][index2] < minValue1){
                    minValue2 = minValue1;
                    minValue1 = distances[index1][index2];
                } else if (distances[index1][index2] < minValue2){
                    minValue2 = distances[index1][index2];
                }
            }
            if ((minValue1 + minValue2) >= bestValue) {
                bestValue = (minValue1 + minValue2);
                bestIndex = index1;
            }
        }
        return new double[] {bestIndex, bestValue};
    }

    public double minkowskiDistance(double[] a, double[] b, double p){
        double value =0;
        for (int i=0; i<a.length; i++)
            value += Math.pow(Math.abs(a[i] - b[i]), p);

        return Math.pow(value, 1.0 / p);
    }


    public double point2LineDistance(double[] P, double[] A, double[] B) {
        double[] pa = new double[P.length];
        double[] ba = new double[P.length];
        for (int i = 0; i < P.length; i++) {
            pa[i] = P[i] - A[i];
            ba[i] = B[i] - A[i];
        }

        double t = dotProduct(pa, ba) / dotProduct(ba, ba);

        double d = 0;
        for (int i = 0; i < P.length; i += 1) {
            d += Math.pow(pa[i] - t * ba[i], 2.0);
        }

        return Math.sqrt(d);
    }

    protected double dotProduct(double[] array1, double[] array2){
        double sum = 0;
        for (int i = 0; i < array1.length; i++) {
            sum += array1[i] * array2[i];
        }
        return sum;
    }

    public static String getAttributeId() {
        return attributeId;
    }

    public List<Double> computeIdealPoint(List<S> population) {
        List<Double> ideal_point;
        ideal_point = new ArrayList<>(numberOfObjectives);

        for (int f = 0; f < numberOfObjectives; f += 1) {
            double minf = Double.MAX_VALUE;
            for (int i = 0; i < population.size(); i += 1) // min values must appear in the first front
            {
                minf = Math.min(minf, population.get(i).objectives()[f]);
            }
            ideal_point.add(minf);
        }

        return ideal_point;
    }

    public List<Double> constructHyperplane(List<S> population, List<S> extreme_points) {
        // Check whether there are duplicate extreme points.
        // This might happen but the original paper does not mention how to deal with it.
        boolean duplicate = false;
        for (int i = 0; !duplicate && i < extreme_points.size(); i += 1) {
            for (int j = i + 1; !duplicate && j < extreme_points.size(); j += 1) {
                duplicate = extreme_points.get(i).equals(extreme_points.get(j));
            }
        }

        List<Double> intercepts = new ArrayList<>();

        if (duplicate) // cannot construct the unique hyperplane (this is a casual method to deal with
        // the condition)
        {
            for (int f = 0; f < numberOfObjectives; f += 1) {
                // extreme_points[f] stands for the individual with the largest value of objective f
                intercepts.add(extreme_points.get(f).objectives()[f]);
            }
        } else {
            // Find the equation of the hyperplane
            List<Double> b = new ArrayList<>(); // (pop[0].objs().size(), 1.0);
            for (int i = 0; i < numberOfObjectives; i++) b.add(1.0);

            List<List<Double>> A = new ArrayList<>();
            for (S s : extreme_points) {
                List<Double> aux = new ArrayList<>();
                for (int i = 0; i < numberOfObjectives; i++) aux.add(s.objectives()[i]);
                A.add(aux);
            }
            List<Double> x = guassianElimination(A, b);

            // Find intercepts
            for (int f = 0; f < numberOfObjectives; f += 1) {
                intercepts.add(1.0 / x.get(f));
            }
        }
        return intercepts;
    }

    public List<Double> guassianElimination(List<List<Double>> A, List<Double> b) {
        List<Double> x = new ArrayList<>();

        int N = A.size();
        for (int i = 0; i < N; i += 1) {
            A.get(i).add(b.get(i));
        }

        for (int base = 0; base < N - 1; base += 1) {
            for (int target = base + 1; target < N; target += 1) {
                double ratio = A.get(target).get(base) / A.get(base).get(base);
                for (int term = 0; term < A.get(base).size(); term += 1) {
                    A.get(target).set(term, A.get(target).get(term) - A.get(base).get(term) * ratio);
                }
            }
        }

        for (int i = 0; i < N; i++) x.add(0.0);

        for (int i = N - 1; i >= 0; i -= 1) {
            for (int known = i + 1; known < N; known += 1) {
                A.get(i).set(N, A.get(i).get(N) - A.get(i).get(known) * x.get(known));
            }
            x.set(i, A.get(i).get(N) / A.get(i).get(i));
        }
        return x;
    }
}
