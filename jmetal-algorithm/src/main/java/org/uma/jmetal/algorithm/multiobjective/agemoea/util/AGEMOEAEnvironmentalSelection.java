package org.uma.jmetal.algorithm.multiobjective.agemoea.util;

import static java.util.Arrays.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/**
 * Environmental Selection used in AGE-MOEA (Adaptive GEometry-based Many-Objective Evolutionary Algorithm)
 *
 * @author Annibale Panichella
 */
public class AGEMOEAEnvironmentalSelection<S extends Solution<?>> {
    protected static @NotNull String attributeId = AGEMOEAEnvironmentalSelection.class.getName();
    protected double P;
    protected List<Double> intercepts;
    protected int numberOfObjectives;


    public AGEMOEAEnvironmentalSelection(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
    }

    public List execute(List<S> solutionList, int solutionsToSelect) throws JMetalException {
        @NotNull Ranking<S> ranking = new FastNonDominatedSortRanking<>() ;
        ranking.compute(solutionList) ;
        return selectFromFronts(ranking, solutionsToSelect);
    }

    protected @NotNull List<S> selectFromFronts(@NotNull Ranking<S> ranking, int solutionsToSelect) {
        List<S> population = new ArrayList<>(solutionsToSelect) ;

        // Apply the survival score for the first front
        this.computeSurvivalScore(ranking.getSubFront(0));
        if (ranking.getSubFront(0).size() <= solutionsToSelect){
            population.addAll(ranking.getSubFront(0));
        } else {
            ranking.getSubFront(0).sort(new SurvivalScoreComparator());
            population.addAll(ranking.getSubFront(0).subList(0, solutionsToSelect));
        }

        // Apply the proximity score for the remaining front
        var rankingIndex = 1;
        while (population.size() < solutionsToSelect) {
            this.assignConvergenceScore(ranking.getSubFront(rankingIndex));
            if ((population.size() + ranking.getSubFront(rankingIndex).size()) <= solutionsToSelect) {
                population.addAll(ranking.getSubFront(rankingIndex));
            } else {
                ranking.getSubFront(rankingIndex).sort(new SurvivalScoreComparator());
                var nSolutions = solutionsToSelect - population.size();
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
        // list to keep track of solutions selected when assigning the survival scores
        @NotNull List<Integer> selected = new ArrayList<>();

        // compute ideal point
        var ideal_point = computeIdealPoint(front);

        // find extreme points
        var extreme_points = this.findExtremes(front);

        if (extreme_points.size() == 0) {
            for (var s : front){
                s.attributes().put(attributeId, 0.0);
            }
            return;
        }

        // set crowding distance for extreme points
        for (var extreme : extreme_points){
            for (var index = 0; index<front.size(); index++){
                if (extreme == front.get(index)){
                    selected.add(index);
                    extreme.attributes().put(attributeId, Double.POSITIVE_INFINITY);
                }
            }
        }

        // compute intercepts for small fronts
        if (front.size() <= this.numberOfObjectives){
            intercepts = new ArrayList<>();
            for(var i = 0; i<this.numberOfObjectives; i++){
                var value = 0.0;
                for (@NotNull S s : front){
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

        for (var i = 0; i<front.size(); i++){
            var objectives = front.get(i).objectives().clone();
            for (var j = 0; j<numberOfObjectives; j++){
                objectives[j] = (objectives[j] - ideal_point.get(j)) / (intercepts.get(j) - ideal_point.get(j));
            }
            normalizedFront.add(i, objectives);
        }

        // compute geometry
        this.P = computeGeometry(normalizedFront, selected);

        // Proximity Score
        var utopia = new double[numberOfObjectives];
        var arr = new double[10];
        var count = 0;
        for (var doubles : normalizedFront) {
            var v = minkowskiDistance(doubles, utopia, P);
            if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
            arr[count++] = v;
        }
        arr = Arrays.copyOfRange(arr, 0, count);
        var nn = arr;

        // Diversity Score
        var distances = pairwiseDistances(normalizedFront, P);

        for (var i = 0; i<normalizedFront.size(); i++) {
            for (var j = 0; j < normalizedFront.size(); j++) {
                distances[i][j] /= nn[i];
            }
        }

        // Survival Score
        List<Integer> remaining = new ArrayList<>();
        var bound = front.size();
        for (var i = 0; i < bound; i++) {
            Integer integer = i;
            remaining.add(integer);
        }
        remaining.removeAll(selected);
        while (remaining.size() > 0){
            var values = this.findMoreDiverseSolution(distances, selected, remaining);
            var index = (int) values[0];
            remaining.remove((Integer) index);
            selected.add(index);
            front.get(index).attributes().put(attributeId, values[1]);
        }
    }

    protected double[][] pairwiseDistances(@NotNull List<double[]> normalizedFront, double P) {
        var distances = new double[normalizedFront.size()][normalizedFront.size()];
        for (var i = 0; i< normalizedFront.size()-1; i++){
            for (var j = i+1; j< normalizedFront.size(); j++){
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
        var utopia = new double[this.numberOfObjectives];

        var arr = new double[10];
        var count = 0;
        var bound = this.numberOfObjectives;
        for (var i1 = 0; i1 < bound; i1++) {
            double v1 = 1;
            if (arr.length == count) arr = Arrays.copyOf(arr, count * 2);
            arr[count++] = v1;
        }
        arr = Arrays.copyOfRange(arr, 0, count);
        var nadir = arr;

        // find central point
        var d = new double[normalizedFront.size()] ;
        for (var i = 0; i<d.length; i++){
            if (extremePoints.contains(i)) {
                d[i] = Double.POSITIVE_INFINITY;
            } else {
                d[i] = this.point2LineDistance(normalizedFront.get(i), utopia, nadir);
            }
        }

        double[] centralPoint = null;
        var minDistance = Double.POSITIVE_INFINITY;
        for (var i = 0; i<d.length; i++){
            if (d[i] < minDistance){
                minDistance = d[i];
                centralPoint = normalizedFront.get(i);
            }
        }
        assert(centralPoint != null);
        double average = 0;
        try {
            var sum = 0.0;
            for (var v : centralPoint) {
                sum += v;
            }
            average = sum / numberOfObjectives;
        } catch(Exception e){
            return 1; // in case of errors, we assume the front to be flat
        }
        var p = Math.log(this.numberOfObjectives) / Math.log(1.0 / average) ;

        if (Double.isNaN(p) || p <0.10 || Double.isInfinite(p))
            p = 1;

        return p;
    }

    /**
     * This method assigns only the convergence/proximity score to solution in the non-dominated front.
     * This score is assigned to non-dominated fronts with ranking index > 0
     * @param front
     */
    protected void assignConvergenceScore(List<S> front){
        var idealPoint = new double[this.numberOfObjectives];
        for (var solution : front){
            var normalizedObjective = solution.objectives().clone();
            for(var i = 0; i<this.numberOfObjectives; i++){
                if (intercepts != null && intercepts.size() == 0)
                    normalizedObjective[i] = normalizedObjective[i] / intercepts.get(i);
            }
            var value =  this.minkowskiDistance(normalizedObjective, idealPoint, P);
            solution.attributes().put(attributeId, value);
        }
    }

    /**
     * This method finds the extreme points in the non-dominated front using point to line (axes) distance
     * @param front the non-dominated front under analysis
     * @return the extreme solutions in the front
     */
    protected List<S> findExtremes(List<S> front) {
        List<S> extremePoints = new ArrayList<>();
        for (var i = 0; i<this.numberOfObjectives; i++){
            var axes = new double[this.numberOfObjectives];
            axes[i] = 1;

            var minDistance = Double.POSITIVE_INFINITY;
            var minIndex = -1;
            for (var j = 0; j<front.size(); j++){
                var dist = this.point2LineDistance(front.get(j).objectives(), new double[this.numberOfObjectives], axes);
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

    protected double[] findMoreDiverseSolution(double[][] distances, @NotNull List<Integer> selected, List<Integer> remaining) {
        double bestValue = 0;
        var bestIndex = -1;

        for (int index1 : remaining){
            var minValue1 = Double.POSITIVE_INFINITY;
            var minValue2 = Double.POSITIVE_INFINITY;

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
        var value = 0.0;
        for (var i = 0; i < a.length; i++) {
            var pow = Math.pow(Math.abs(a[i] - b[i]), p);
            value += pow;
        }

        return Math.pow(value, 1.0 / p);
    }


    /**
     * This method compute the point to line distance
     * @param P point to consider
     * @param A First point delimiting the line (direction)
     * @param B Second point delimiting the line (direction)
     * @return the perpendicular distance between P and the line AB
     */
    public double point2LineDistance(double @NotNull [] P, double[] A, double[] B) {
        var pa = new double[P.length];
        var ba = new double[P.length];
        for (var i = 0; i < P.length; i++) {
            pa[i] = P[i] - A[i];
            ba[i] = B[i] - A[i];
        }

        var t = dotProduct(pa, ba) / dotProduct(ba, ba);

        var d = 0.0;
        for (var i = 0; i < P.length; i++) {
            var pow = Math.pow(pa[i] - t * ba[i], 2.0);
            d += pow;
        }

        return Math.sqrt(d);
    }

    /**
     * Small method to compute the dot product between to input arrays/vectors
     * @param array1 First vector
     * @param array2 Second vector
     * @return the doc product
     */
    protected double dotProduct(double @NotNull [] array1, double[] array2){
        var sum = 0.0;
        for (var i = 0; i < array1.length; i++) {
            var v = array1[i] * array2[i];
            sum += v;
        }
        return sum;
    }

    public static String getAttributeId() {
        return attributeId;
    }

    public @NotNull List<Double> computeIdealPoint(List<S> population) {
        List<Double> ideal_point = new ArrayList<>(numberOfObjectives);

        for (var f = 0; f < numberOfObjectives; f += 1) {
            var minf = Double.MAX_VALUE;
            for (var i = 0; i < population.size(); i += 1) // min values must appear in the first front
            {
                minf = Math.min(minf, population.get(i).objectives()[f]);
            }
            ideal_point.add(minf);
        }

        return ideal_point;
    }

    public List<Double> constructHyperplane(List<S> population, @NotNull List<S> extreme_points) {
        // Check whether there are duplicate extreme points.
        // This might happen but the original paper does not mention how to deal with it.
        var duplicate = false;
        for (var i = 0; !duplicate && i < extreme_points.size(); i += 1) {
            for (var j = i + 1; !duplicate && j < extreme_points.size(); j += 1) {
                duplicate = extreme_points.get(i).equals(extreme_points.get(j));
            }
        }

        List<Double> intercepts;

        if (duplicate) // cannot construct the unique hyperplane (this is a casual method to deal with
        // the condition)
        {
            // extreme_points[f] stands for the individual with the largest value of objective f
            List<Double> list = new ArrayList<>();
            var bound = numberOfObjectives;
            for (var f = 0; f < bound; f++) {
                @NotNull Double objective = extreme_points.get(f).objectives()[f];
                list.add(objective);
            }
            intercepts = list;
        } else {
            // Find the equation of the hyperplane
            // (pop[0].objs().size(), 1.0);
            List<Double> b = new ArrayList<>();
            var bound1 = numberOfObjectives;
            for (var i1 = 0; i1 < bound1; i1++) {
                @NotNull Double aDouble1 = 1.0;
                b.add(aDouble1);
            }

            @NotNull List<List<Double>> A = extreme_points.stream().<List<Double>>map(s -> {
                List<Double> list = new ArrayList<>();
                var array = s.objectives();
                var bound = numberOfObjectives;
                for (var i = 0; i < bound; i++) {
                    var v = array[i];
                    @NotNull Double aDouble = v;
                    list.add(aDouble);
                }
                return list;
            }).collect(Collectors.toList());
            var x = guassianElimination(A, b);

            // Find intercepts
            @NotNull List<Double> list = new ArrayList<>();
            var bound = numberOfObjectives;
            for (var f = 0; f < bound; f++) {
                @NotNull Double aDouble = 1.0 / x.get(f);
                list.add(aDouble);
            }
            intercepts = list;
        }
        return intercepts;
    }

    public List<Double> guassianElimination(List<List<Double>> A, @NotNull List<Double> b) {

        var N = A.size();
        for (var i = 0; i < N; i += 1) {
            A.get(i).add(b.get(i));
        }

        for (var base = 0; base < N - 1; base += 1) {
            for (var target = base + 1; target < N; target += 1) {
                var ratio = A.get(target).get(base) / A.get(base).get(base);
                for (var term = 0; term < A.get(base).size(); term += 1) {
                    A.get(target).set(term, A.get(target).get(term) - A.get(base).get(term) * ratio);
                }
            }
        }

        List<Double> list = new ArrayList<>();
        for (var i1 = 0; i1 < N; i1++) {
            Double aDouble = 0.0;
            list.add(aDouble);
        }
        var x = list;

        for (var i = N - 1; i >= 0; i -= 1) {
            for (var known = i + 1; known < N; known += 1) {
                A.get(i).set(N, A.get(i).get(N) - A.get(i).get(known) * x.get(known));
            }
            x.set(i, A.get(i).get(N) / A.get(i).get(i));
        }
        return x;
    }
}
