package org.uma.jmetal.util;

import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/** Created by Antonio J. Nebro on 6/12/14. */
public class SolutionUtils {


  /**
     * Return the best solution between those passed as arguments. If they are equal or incomparable
     * one of them is chosen randomly.
     *
     * @return The best solution
     */
  public static <S extends Solution<?>> S getBestSolution(
      S solution1, S solution2, Comparator<S> comparator) {
    return getBestSolution(
        solution1, solution2, comparator, () -> JMetalRandom.getInstance().nextDouble());
  }

  /**
   * Return the best solution between those passed as arguments. If they are equal or incomparable
   * one of them is chosen randomly.
   *
   * @param randomGenerator {@link RandomGenerator} for the equality case
   * @return The best solution
   */
  public static <S extends Solution<?>> S getBestSolution(
          S solution1, S solution2, @NotNull Comparator<S> comparator, @NotNull RandomGenerator<Double> randomGenerator) {
    return getBestSolution(
        solution1, solution2, comparator, (a, b) -> randomGenerator.getRandomValue() < 0.5 ? a : b);
  }

  /**
   * Return the best solution between those passed as arguments. If they are equal or incomparable
   * one of them is chosen based on the given policy.
   *
   * @return The best solution
   */
  public static <S extends Solution<?>> S getBestSolution(
          S solution1, S solution2, @NotNull Comparator<S> comparator, BinaryOperator<S> equalityPolicy) {
    S result;
    var flag = comparator.compare(solution1, solution2);
    if (flag == -1) {
      result = solution1;
    } else if (flag == 1) {
      result = solution2;
    } else {
      result = equalityPolicy.apply(solution1, solution2);
    }

    return result;
  }

  /** Returns the euclidean distance between a pair of solutions in the objective space */
  public static <S extends Solution<?>> double distanceBetweenObjectives(
      S firstSolution, S secondSolution) {

    double diff;
    var distance = 0.0;

    // euclidean distance
    for (var nObj = 0; nObj < firstSolution.objectives().length; nObj++) {
      diff = firstSolution.objectives()[nObj] - secondSolution.objectives()[nObj];
      distance += Math.pow(diff, 2.0);
    }

    return Math.sqrt(distance);
  }

  public static <S extends Solution<?>> double normalizedDistanceBetweenObjectives(
      S firstSolution, S secondSolution, double[] maxs, double[] mins) {

    double diff;
    var distance = 0.0;
    // euclidean distance
    for (var nObj = 0; nObj < firstSolution.objectives().length; nObj++) {
      diff =
          (firstSolution.objectives()[nObj] / (maxs[nObj] - mins[nObj]))
              - (secondSolution.objectives()[nObj] / (maxs[nObj] - mins[nObj]));
      distance += Math.pow(diff, 2.0);
    }

    return Math.sqrt(distance);
  }

  /**
   * Returns the minimum distance from a <code>Solution</code> to a <code>SolutionSet according to
   * the encodings.variable values</code>.
   *
   * @param solution The <code>Solution</code>.
   * @param solutionList The <code>List<Solution></></code>.
   * @return The minimum distance between solution and the set.
   */
  public static double distanceToSolutionListInSolutionSpace(
          @NotNull DoubleSolution solution, List<DoubleSolution> solutionList) {
    var distance = Double.MAX_VALUE;

    for (var i = 0; i < solutionList.size(); i++) {
      var aux = distanceBetweenSolutionsInObjectiveSpace(solution, solutionList.get(i));
      if (aux < distance) distance = aux;
    }

    return distance;
  }

  /**
   * Returns the distance between two solutions in the search space.
   *
   * @param solutionI The first <code>Solution</code>.
   * @param solutionJ The second <code>Solution</code>.
   * @return the distance between solutions.
   */
  public static double distanceBetweenSolutionsInObjectiveSpace(
      DoubleSolution solutionI, DoubleSolution solutionJ) {
    var distance = 0.0;

    double diff;
    for (var i = 0; i < solutionI.variables().size(); i++) {
      diff = solutionI.variables().get(i) - solutionJ.variables().get(i);
      distance += Math.pow(diff, 2.0);
    }

    return Math.sqrt(distance);
  }

  /** Returns the average euclidean distance of a solution to the solutions of a list. */
  public static <S extends Solution<?>> double averageDistanceToSolutionList(
      S solution, List<S> solutionList) {

    var sumOfDistances = 0.0;
      for (var sol : solutionList) {
        var v = distanceBetweenObjectives(solution, sol);
          sumOfDistances += v;
      }

      return sumOfDistances / solutionList.size();
  }

  /**
   * It returns the normalized solution given the minimum and maximum values for each objective
   *
   * @param solution to be normalized
   * @param minValues minimum values for each objective
   * @param maxValues maximum value for each objective
   * @return normalized solution
   */
  public static <S extends Solution<?>> S normalize(S solution, double @Nullable [] minValues, double[] maxValues) {

    if (solution == null) {
      throw new JMetalException("The solution should not be null");
    }

    if (minValues == null || maxValues == null) {
      throw new JMetalException("The minValues and maxValues should not be null");
    }

    if (minValues.length == 0 || maxValues.length == 0) {
      throw new JMetalException("The minValues and maxValues should not be empty");
    }

    if (minValues.length != maxValues.length) {
      throw new JMetalException("The minValues and maxValues should have the same length");
    }

    if (solution.objectives().length != minValues.length) {
      throw new JMetalException(
          "The number of objectives should be the same to min and max length");
    }

    var copy = (S) solution.copy();

    for (var i = 0; i < copy.objectives().length; i++) {
      copy.objectives()[i] = NormalizeUtils.normalize(solution.objectives()[i], minValues[i], maxValues[i]);
    }

    return copy;
  }
}
