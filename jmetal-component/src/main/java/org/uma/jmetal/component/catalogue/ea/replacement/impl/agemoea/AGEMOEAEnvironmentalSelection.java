package org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea;

import static java.util.Arrays.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/**
 * Environmental Selection used in AGE-MOEA (Adaptive GEometry-based Many-Objective Evolutionary Algorithm)
 *
 * <p>The population is sorted into fronts with a {@link Ranking}: by default, the fast
 * non-dominated sorting with the constrained dominance (Deb's constraint-domination principle).
 * Another ranking, such as one with another constraint-handling technique, can be given to the
 * constructor.
 *
 * @author Annibale Panichella
 */
public class AGEMOEAEnvironmentalSelection<S extends Solution<?>> {
    protected static String attributeId = AGEMOEAEnvironmentalSelection.class.getName();
    protected double P;
    protected List<Double> intercepts;
    protected List<Double> idealPoint;
    protected int numberOfObjectives;
    private final Ranking<S> ranking;


    public AGEMOEAEnvironmentalSelection(int numberOfObjectives) {
        this(numberOfObjectives, new FastNonDominatedSortRanking<>(new DominanceWithConstraintsComparator<>()));
    }

    /**
     * @param numberOfObjectives the number of objectives of the problem
     * @param ranking the ranking that sorts the population into fronts
     */
    public AGEMOEAEnvironmentalSelection(int numberOfObjectives, Ranking<S> ranking) {
        Check.notNull(ranking, "ranking");
        this.numberOfObjectives = numberOfObjectives;
        this.ranking = ranking;
    }

    /**
     * @return the ranking that sorts the population into fronts, whose ranks the
     *     {@link SurvivalScoreComparator} of the mating selection must read
     */
    public Ranking<S> ranking() {
        return ranking;
    }

    public List<S> execute(List<S> solutionList, int solutionsToSelect) throws JMetalException {
        ranking.compute(solutionList) ;
        return selectFromFronts(ranking, solutionsToSelect);
    }

    protected List<S> selectFromFronts(Ranking<S> ranking, int solutionsToSelect) {
        List<S> population = new ArrayList<>(solutionsToSelect) ;

        // Apply the survival score for the first front
        this.computeSurvivalScore(ranking.getSubFront(0));
        if (ranking.getSubFront(0).size() <= solutionsToSelect){
            population.addAll(ranking.getSubFront(0));
        } else {
            ranking.getSubFront(0).sort(new SurvivalScoreComparator<>(ranking));
            population.addAll(ranking.getSubFront(0).subList(0, solutionsToSelect));
        }

        // Apply the proximity score for the remaining front
        int rankingIndex = 1;
        while (population.size() < solutionsToSelect) {
            this.assignConvergenceScore(ranking.getSubFront(rankingIndex));
            if ((population.size() + ranking.getSubFront(rankingIndex).size()) <= solutionsToSelect) {
                population.addAll(ranking.getSubFront(rankingIndex));
            } else {
                ranking.getSubFront(rankingIndex).sort(new SurvivalScoreComparator<>(ranking));
                int nSolutions = solutionsToSelect - population.size();
                population.addAll(ranking.getSubFront(rankingIndex).subList(0, nSolutions));
            }
            rankingIndex++;
        }

        return population ;
    }

    /**
     * This method compute/assigns the survival scores to the solutions in the given front
     * @param front
     */
    public void computeSurvivalScore(List<S> front){
        resetState();

        // list to keep track of solutions selected when assigning the survival scores
        List<Integer> selected = new ArrayList<>();

        // compute ideal point
        List<Double> ideal_point = computeIdealPoint(front);
        idealPoint = ideal_point;

        // find extreme points
        List<S> extreme_points = this.findExtremes(front);

        if (extreme_points.size() == 0) {
            for (S s : front){
                s.attributes().put(attributeId, 0.0);
            }
            return;
        }

        // set crowding distance for extreme points
        for (S extreme : extreme_points){
            for (int index = 0; index<front.size(); index++){
                if (extreme == front.get(index)){
                    selected.add(index);
                    extreme.attributes().put(attributeId, Double.POSITIVE_INFINITY);
                }
            }
        }

        // compute intercepts for small fronts
        if (front.size() <= this.numberOfObjectives){
            intercepts = new ArrayList<>();
            for(int i=0; i<this.numberOfObjectives; i++){
                double value = 0.0;
                for (S s : front){
                    if (s.objectives()[i] - ideal_point.get(i) > value)
                        value = s.objectives()[i] - ideal_point.get(i);
                }
                intercepts.add(value > 0.0 ? value : 1.0);
            }
            return;
        }


        // normalize the front
        List<double[]> normalizedFront = new ArrayList<>();
        intercepts = constructHyperplane(front, extreme_points);

        for (int i=0; i<front.size(); i++){
            double[] objectives = front.get(i).objectives().clone();
            for (int j=0; j<numberOfObjectives; j++){
                objectives[j] = (objectives[j] - ideal_point.get(j)) / intercepts.get(j);
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

        // Diversity Score
        double[][] distances = pairwiseDistances(normalizedFront, P);

        for (int i=0; i<normalizedFront.size(); i++) {
            for (int j = 0; j < normalizedFront.size(); j++) {
                distances[i][j] /= nn[i];
            }
        }

        // Survival Score
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

    protected double[][] pairwiseDistances(List<double[]> normalizedFront, double P) {
        double[][] distances = new double[normalizedFront.size()][normalizedFront.size()];
        for (int i = 0; i< normalizedFront.size()-1; i++){
            for (int j = i+1; j< normalizedFront.size(); j++){
                distances[i][j] = this.minkowskiDistance(normalizedFront.get(i), normalizedFront.get(j), P);
                distances[j][i] = distances[i][j];
            }
        }
        return distances;
    }

    /**
     * This method compute the front/shape of the front using the central point as the reference point
     * @param normalizedFront normalized non-dominated front
     * @param extremePoints extreme points of the non-dominated front
     * @return
     */
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
        if (centralPoint == null)
            return 1;
        double average = 0;
        try {
            average = stream(centralPoint).sum() / numberOfObjectives;
        } catch(Exception e){
            return 1; // in case of errors, we assume the front to be flat
        }
        double p = Math.log(this.numberOfObjectives) / Math.log(1.0 / average) ;

        if (Double.isNaN(p) || p <0.10 || Double.isInfinite(p))
            p = 1;

        return p;
    }

    /**
     * This method assigns only the convergence/proximity score to solution in the non-dominated front.
     * This score is assigned to non-dominated fronts with ranking index > 0: the inverse of the
     * Minkowski distance (of order P) of the solution to the ideal point of the first front, with the
     * objectives normalized as those of the first front, so that the solutions closer to the ideal
     * point have higher scores (Panichella, GECCO 2019, and the authors' code in PlatEMO)
     * @param front
     */
    protected void assignConvergenceScore(List<S> front){
        double[] origin = new double[this.numberOfObjectives];
        for (S solution : front){
            double[] normalizedObjective = solution.objectives().clone();
            for(int i=0; i<this.numberOfObjectives; i++){
                if (idealPoint != null && !idealPoint.isEmpty())
                    normalizedObjective[i] = normalizedObjective[i] - idealPoint.get(i);
                if (intercepts != null && !intercepts.isEmpty())
                    normalizedObjective[i] = normalizedObjective[i] / intercepts.get(i);
            }
            double distance = this.minkowskiDistance(normalizedObjective, origin, P);
            solution.attributes().put(attributeId, distance > 0.0 ? 1.0 / distance : Double.POSITIVE_INFINITY);
        }
    }

    /**
     * This method finds the extreme points in the non-dominated front using point to line (axes) distance,
     * with the objectives translated so that the ideal point of the front is the origin
     * @param front the non-dominated front under analysis
     * @return the extreme solutions in the front
     */
    protected List<S> findExtremes(List<S> front) {
        List<Double> ideal = computeIdealPoint(front);
        List<S> extremePoints = new ArrayList<>();
        for (int i=0; i<this.numberOfObjectives; i++){
            double[] axes = new double[this.numberOfObjectives];
            axes[i] = 1;

            double minDistance = Double.POSITIVE_INFINITY;
            int minIndex = -1;
            for (int j=0; j<front.size(); j++){
                double dist = this.point2LineDistance(translated(front.get(j), ideal), new double[this.numberOfObjectives], axes);
                if (dist < minDistance){
                    minDistance = dist;
                    minIndex = j;
                }
            }
            if (minIndex != -1)
                extremePoints.add(front.get(minIndex));
        }
        return extremePoints;
    }

    protected double[] findMoreDiverseSolution(double[][] distances, List<Integer> selected, List<Integer> remaining) {
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


    /**
     * This method compute the point to line distance
     * @param P point to consider
     * @param A First point delimiting the line (direction)
     * @param B Second point delimiting the line (direction)
     * @return the perpendicular distance between P and the line AB
     */
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

    /**
     * Small method to compute the dot product between to input arrays/vectors
     * @param array1 First vector
     * @param array2 Second vector
     * @return the doc product
     */
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

    private void resetState() {
        P = 1.0;
        intercepts = null;
        idealPoint = null;
    }

    /**
     * @return the objectives of the solution minus the given ideal point
     */
    protected double[] translated(S solution, List<Double> ideal) {
        double[] objectives = solution.objectives().clone();
        for (int i = 0; i < objectives.length; i++) {
            objectives[i] -= ideal.get(i);
        }
        return objectives;
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

    /**
     * The intercepts of the hyperplane through the extreme points, with the objectives translated so
     * that the ideal point of the population is the origin. As in the authors' code (PlatEMO), they
     * are the maxima of the translated objectives when the extreme points are not distinct or the
     * hyperplane has no finite positive intercepts; an intercept of zero is taken as one.
     *
     * @param population the front to normalize
     * @param extreme_points its extreme points
     * @return the intercepts, relative to the ideal point of the population
     */
    public List<Double> constructHyperplane(List<S> population, List<S> extreme_points) {
        List<Double> ideal = computeIdealPoint(population);
        // Check whether there are duplicate extreme points.
        // This might happen but the original paper does not mention how to deal with it.
        boolean duplicate = false;
        for (int i = 0; !duplicate && i < extreme_points.size(); i += 1) {
            for (int j = i + 1; !duplicate && j < extreme_points.size(); j += 1) {
                duplicate = extreme_points.get(i).equals(extreme_points.get(j));
            }
        }

        List<Double> intercepts = new ArrayList<>();
        boolean valid = !duplicate;

        if (!duplicate) {
            // Find the equation of the hyperplane
            List<Double> b = new ArrayList<>(); // (pop[0].objs().size(), 1.0);
            for (int i = 0; i < numberOfObjectives; i++) b.add(1.0);

            List<List<Double>> A = new ArrayList<>();
            for (S s : extreme_points) {
                List<Double> aux = new ArrayList<>();
                double[] objectives = translated(s, ideal);
                for (int i = 0; i < numberOfObjectives; i++) aux.add(objectives[i]);
                A.add(aux);
            }
            List<Double> x = guassianElimination(A, b);

            // Find intercepts
            for (int f = 0; f < numberOfObjectives; f += 1) {
                double intercept = 1.0 / x.get(f);
                valid &= x.get(f) > 0.0 && Double.isFinite(x.get(f)) && Double.isFinite(intercept);
                intercepts.add(intercept);
            }
        }

        if (!valid) {
            // the maxima of the translated objectives
            intercepts = new ArrayList<>();
            for (int f = 0; f < numberOfObjectives; f += 1) {
                double maximum = 0.0;
                for (S s : population) {
                    maximum = Math.max(maximum, s.objectives()[f] - ideal.get(f));
                }
                intercepts.add(maximum);
            }
        }
        for (int f = 0; f < numberOfObjectives; f += 1) {
            if (intercepts.get(f) == 0.0)
                intercepts.set(f, 1.0);
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
