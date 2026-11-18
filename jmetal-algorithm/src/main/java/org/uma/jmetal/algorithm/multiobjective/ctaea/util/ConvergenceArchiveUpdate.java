package org.uma.jmetal.algorithm.multiobjective.ctaea.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Update of the convergence archive (CA) of C-TAEA, transcribed from the {@code UpdateCA}
 * function of PlatEMO. The candidates are the current CA and the offspring, and the new CA has
 * as many solutions as weight vectors (N):
 *
 * <ul>
 *   <li>If exactly N candidates are feasible, they are the new CA.
 *   <li>If more than N candidates are feasible, the fronts of their non-dominated sorting (on the
 *       objectives) are added until there are at least N solutions, and then one solution is
 *       removed at a time until N remain: the objectives of the remaining solutions are
 *       normalized with their ideal and nadir points, the solutions are associated with the
 *       subregions on the normalized objectives, and in the most crowded subregion (the first one
 *       in case of a tie) the solutions at the smallest Euclidean distance from another one (on
 *       the raw objectives, ignoring zero distances) are the candidates to be removed; among
 *       them, the one with the largest Tchebycheff value is removed, computed with the weight
 *       vector of its subregion (associated on the raw objectives) and the ideal point of the
 *       candidates.
 *   <li>If fewer than N candidates are feasible, all of them are kept, and the infeasible ones
 *       are sorted by non-dominated sorting on two values to minimize: the constraint violation
 *       and the Tchebycheff value, computed with the weight vector of its subregion (associated
 *       on the raw objectives) and the ideal point of the infeasible solutions. The fronts are
 *       added until there are at least N solutions, and the solutions of the last front with the
 *       largest constraint violation are removed (the first ones in case of a tie).
 * </ul>
 *
 * <p>Solutions are compared by identity, never with {@code equals}. Unlike the {@code setdiff}
 * function of MATLAB, which also sorts the set, the order of the candidates is kept; it only
 * matters to break ties.
 *
 * @param <S> the type of the solutions
 */
public class ConvergenceArchiveUpdate<S extends Solution<?>> {
  private final double[][] weights;

  /**
   * Creates the update.
   *
   * @param weights the weight vectors; their number is the size of the archive
   */
  public ConvergenceArchiveUpdate(double[][] weights) {
    Check.notNull(weights, "weights");
    Check.that(weights.length > 0, "There must be at least one weight vector");
    this.weights = weights;
  }

  /**
   * Computes the new convergence archive.
   *
   * @param convergenceArchive the current archive (empty at the beginning)
   * @param offspring the new solutions, already evaluated
   * @return the new archive, with as many solutions as weight vectors if there are enough
   *     candidates (or all of them otherwise)
   */
  public List<S> update(List<S> convergenceArchive, List<S> offspring) {
    Check.notNull(convergenceArchive, "convergenceArchive");
    Check.notNull(offspring, "offspring");

    List<S> feasible = new ArrayList<>();
    List<S> infeasible = new ArrayList<>();
    for (List<S> list : List.of(convergenceArchive, offspring)) {
      for (S solution : list) {
        (ConstraintViolation.isFeasible(solution) ? feasible : infeasible).add(solution);
      }
    }

    int size = weights.length;
    List<S> result;
    if (feasible.size() == size) {
      result = feasible;
    } else if (feasible.size() > size) {
      result = selectAmongFeasible(feasible);
    } else {
      result = completeWithInfeasible(feasible, infeasible);
    }
    return result;
  }

  private List<S> selectAmongFeasible(List<S> feasible) {
    int[] fronts =
        NonDominatedSorting.frontNumbers(SolutionListUtils.getMatrixWithObjectiveValues(feasible));
    List<S> selected = new ArrayList<>();
    addFrontsUntilFull(feasible, fronts, selected);
    while (selected.size() > weights.length) {
      removeByIdentity(selected, worstOfTheMostCrowdedSubregion(selected));
    }
    return selected;
  }

  /** Returns the solution that the truncation of a set of feasible solutions removes. */
  S worstOfTheMostCrowdedSubregion(List<S> solutions) {
    double[][] objectives = SolutionListUtils.getMatrixWithObjectiveValues(solutions);
    int[] regions = Subregions.associate(normalize(objectives), weights);

    int[] counter = new int[weights.length];
    for (int region : regions) {
      counter[region]++;
    }
    int mostCrowded = 0;
    for (int i = 1; i < counter.length; i++) {
      if (counter[i] > counter[mostCrowded]) {
        mostCrowded = i;
      }
    }

    List<S> crowded = new ArrayList<>();
    for (int i = 0; i < solutions.size(); i++) {
      if (regions[i] == mostCrowded) {
        crowded.add(solutions.get(i));
      }
    }

    List<S> closest = closestSolutions(crowded);
    double[] tchebycheff =
        Subregions.tchebycheffInOwnSubregion(
            SolutionListUtils.getMatrixWithObjectiveValues(closest), weights);
    return closest.get(Subregions.indexOfMaximum(tchebycheff));
  }

  /**
   * Returns the solutions at the minimum distance, with repetitions, in the order of MATLAB's
   * {@code find} on the distance matrix (column by column). Zero distances (equal objective
   * vectors and the diagonal) are ignored; if all of them are zero, every pair is returned.
   */
  private List<S> closestSolutions(List<S> solutions) {
    double[][] objectives = SolutionListUtils.getMatrixWithObjectiveValues(solutions);
    int n = solutions.size();
    double[][] distance = new double[n][n];
    double minimum = Double.POSITIVE_INFINITY;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        double value = euclideanDistance(objectives[i], objectives[j]);
        distance[i][j] = value == 0.0 ? Double.POSITIVE_INFINITY : value;
        minimum = Math.min(minimum, distance[i][j]);
      }
    }

    List<S> closest = new ArrayList<>();
    for (int j = 0; j < n; j++) {
      for (int i = 0; i < n; i++) {
        if (distance[i][j] == minimum) {
          closest.add(solutions.get(i));
        }
      }
    }
    return closest;
  }

  private List<S> completeWithInfeasible(List<S> feasible, List<S> infeasible) {
    List<S> selected = new ArrayList<>(feasible);
    if (infeasible.isEmpty()) {
      return selected;
    }

    double[][] objectives = SolutionListUtils.getMatrixWithObjectiveValues(infeasible);
    double[] tchebycheff = Subregions.tchebycheffInOwnSubregion(objectives, weights);
    double[][] criteria = new double[infeasible.size()][];
    for (int i = 0; i < infeasible.size(); i++) {
      criteria[i] = new double[] {ConstraintViolation.of(infeasible.get(i)), tchebycheff[i]};
    }
    int[] fronts = NonDominatedSorting.frontNumbers(criteria);

    int lastFront = addFrontsUntilFull(infeasible, fronts, selected);
    if (selected.size() > weights.length) {
      List<S> last = new ArrayList<>();
      for (int i = 0; i < infeasible.size(); i++) {
        if (fronts[i] == lastFront) {
          last.add(infeasible.get(i));
        }
      }
      // A stable sort, as MATLAB's sort(CV, 'descend'): ties keep their order
      last.sort(Comparator.comparingDouble((S s) -> ConstraintViolation.of(s)).reversed());
      int excess = selected.size() - weights.length;
      for (S solution : last.subList(0, excess)) {
        removeByIdentity(selected, solution);
      }
    }
    return selected;
  }

  /**
   * Adds the solutions of the fronts 1, 2, ... to a list until it has at least as many solutions
   * as weight vectors (or all of them have been added).
   *
   * @return the number of the last front added
   */
  private int addFrontsUntilFull(List<S> solutions, int[] fronts, List<S> selected) {
    int maxFront = 0;
    for (int front : fronts) {
      maxFront = Math.max(maxFront, front);
    }
    int front = 0;
    while (front < maxFront && selected.size() < weights.length) {
      front++;
      for (int i = 0; i < solutions.size(); i++) {
        if (fronts[i] == front) {
          selected.add(solutions.get(i));
        }
      }
    }
    return front;
  }

  /** Normalizes the objectives with their ideal and nadir points, as PlatEMO (no safeguard). */
  private static double[][] normalize(double[][] objectives) {
    double[] ideal = Subregions.idealPoint(objectives);
    double[] nadir = Subregions.nadirPoint(objectives);
    double[][] normalized = new double[objectives.length][];
    for (int i = 0; i < objectives.length; i++) {
      normalized[i] = new double[ideal.length];
      for (int j = 0; j < ideal.length; j++) {
        normalized[i][j] = (objectives[i][j] - ideal[j]) / (nadir[j] - ideal[j]);
      }
    }
    return normalized;
  }

  private static double euclideanDistance(double[] a, double[] b) {
    double sum = 0.0;
    for (int j = 0; j < a.length; j++) {
      double difference = a[j] - b[j];
      sum += difference * difference;
    }
    return Math.sqrt(sum);
  }

  private static <T> void removeByIdentity(List<T> list, T element) {
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i) == element) {
        list.remove(i);
        return;
      }
    }
  }
}
