package org.uma.jmetal.algorithm.multiobjective.ccmo;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;

/**
 * Fitness of CCMO, as in the {@code CalFitness} function of the PlatEMO implementation: the fitness
 * of SPEA2 with a dominance relation in which the constraint violation comes first.
 *
 * <p>A solution dominates another one if its constraint violation is smaller or, when both have
 * the same violation, if it dominates it in the Pareto sense. The strength {@code S(i)} of a
 * solution is the number of solutions that it dominates, its raw fitness {@code R(i)} is the sum of
 * the strengths of the solutions that dominate it, and its density is {@code D(i) = 1 / (d + 2)},
 * where {@code d} is the Euclidean distance in the objective space to its k-th nearest neighbour,
 * with {@code k = floor(sqrt(n))} and {@code n} the number of solutions. The fitness, to be
 * minimized, is {@code R(i) + D(i)}, so that the solutions that no other one dominates are those
 * with a fitness lower than 1. The objectives are not normalized.
 *
 * <p>Without constraint violations (all zero) this is the fitness of SPEA2.
 */
final class CCMOFitness {

  private CCMOFitness() {}

  /**
   * Computes the fitness of a list of evaluated solutions.
   *
   * @param solutions the solutions
   * @param withConstraints whether the constraint violation of the solutions is taken into account
   *     (the original problem) or ignored (the helper problem, without constraints)
   * @return the fitness of each solution, in the order of the list
   */
  static <S extends Solution<?>> double[] compute(List<S> solutions, boolean withConstraints) {
    double[] violations =
        withConstraints ? constraintViolations(solutions) : new double[solutions.size()];
    return compute(objectives(solutions), violations);
  }

  /**
   * Computes the fitness of a set of objective vectors with their constraint violations.
   *
   * @param objectives the objective vectors (one per row)
   * @param violations the constraint violation of each vector (zero if feasible)
   * @return the fitness of each vector
   */
  static double[] compute(double[][] objectives, double[] violations) {
    int n = objectives.length;
    boolean[][] dominates = dominanceMatrix(objectives, violations);

    int[] strength = new int[n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (dominates[i][j]) {
          strength[i]++;
        }
      }
    }

    double[][] distances = distances(objectives);
    int k = (int) Math.floor(Math.sqrt(n));
    double[] fitness = new double[n];
    for (int i = 0; i < n; i++) {
      double raw = 0.0;
      for (int j = 0; j < n; j++) {
        if (dominates[j][i]) {
          raw += strength[j];
        }
      }
      double[] sortedDistances = distances[i].clone();
      Arrays.sort(sortedDistances);
      fitness[i] = raw + 1.0 / (sortedDistances[k - 1] + 2.0);
    }
    return fitness;
  }

  /**
   * Returns the constraint violation of each solution: the absolute value of its overall constraint
   * violation degree, which is the sum of the violations of its constraints in the jMetal
   * convention (a constraint value lower than zero is violated), or the value precomputed by the
   * problem.
   */
  static <S extends Solution<?>> double[] constraintViolations(List<S> solutions) {
    return solutions.stream()
        .mapToDouble(s -> Math.abs(ConstraintHandling.overallConstraintViolationDegree(s)))
        .toArray();
  }

  /** Returns the objective vectors of the solutions, one per row. */
  static <S extends Solution<?>> double[][] objectives(List<S> solutions) {
    return solutions.stream().map(Solution::objectives).toArray(double[][]::new);
  }

  /**
   * Returns the Euclidean distances between the objective vectors, with an infinite distance of
   * each vector to itself.
   */
  static double[][] distances(double[][] objectives) {
    int n = objectives.length;
    double[][] distances = new double[n][n];
    for (int i = 0; i < n; i++) {
      distances[i][i] = Double.POSITIVE_INFINITY;
      for (int j = i + 1; j < n; j++) {
        double sum = 0.0;
        for (int m = 0; m < objectives[i].length; m++) {
          double difference = objectives[i][m] - objectives[j][m];
          sum += difference * difference;
        }
        distances[i][j] = Math.sqrt(sum);
        distances[j][i] = distances[i][j];
      }
    }
    return distances;
  }

  private static boolean[][] dominanceMatrix(double[][] objectives, double[] violations) {
    int n = objectives.length;
    boolean[][] dominates = new boolean[n][n];
    for (int i = 0; i < n - 1; i++) {
      for (int j = i + 1; j < n; j++) {
        if (violations[i] < violations[j]) {
          dominates[i][j] = true;
        } else if (violations[i] > violations[j]) {
          dominates[j][i] = true;
        } else {
          int comparison = paretoComparison(objectives[i], objectives[j]);
          if (comparison < 0) {
            dominates[i][j] = true;
          } else if (comparison > 0) {
            dominates[j][i] = true;
          }
        }
      }
    }
    return dominates;
  }

  /** Returns -1 if a dominates b, 1 if b dominates a, and 0 otherwise. */
  private static int paretoComparison(double[] a, double[] b) {
    boolean aIsBetterInSomeObjective = false;
    boolean bIsBetterInSomeObjective = false;
    for (int m = 0; m < a.length; m++) {
      if (a[m] < b[m]) {
        aIsBetterInSomeObjective = true;
      } else if (a[m] > b[m]) {
        bIsBetterInSomeObjective = true;
      }
    }
    int comparison = 0;
    if (aIsBetterInSomeObjective && !bIsBetterInSomeObjective) {
      comparison = -1;
    } else if (bIsBetterInSomeObjective && !aIsBetterInSomeObjective) {
      comparison = 1;
    }
    return comparison;
  }
}
