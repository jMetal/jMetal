package org.uma.jmetal.algorithm.multiobjective.top;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/**
 * The parent and environmental selection of phase 2 of ToP, NSGA-II with the
 * constrained-domination principle, as in PlatEMO ({@code EnvironmentalSelection.m} of ToP, with
 * {@code NDSort.m}, {@code CrowdingDistance.m} and {@code TournamentSelection.m}).
 *
 * <p>The front numbers start at zero (at one in PlatEMO). Ties are broken by the position of the
 * solutions in the list, the first one being preferred, and NaN crowding distances, which an
 * objective with a single value in a front gives, are the first ones in the truncation and the
 * worst ones in the tournament, as MATLAB sorts them in descending and ascending order.
 */
final class ToPSelection {
  private static final DefaultDominanceComparator<DoubleSolution> PARETO_DOMINANCE =
      new DefaultDominanceComparator<>();

  private ToPSelection() {}

  /**
   * The constrained dominance of PlatEMO's {@code NDSort}: two feasible solutions are compared by
   * Pareto dominance; otherwise, the solution with the smaller overall constraint violation
   * dominates the other one (so a feasible solution dominates an infeasible one), and two
   * infeasible solutions with the same violation are non-dominated, whatever their objectives.
   *
   * @param solution1 a solution
   * @param solution2 another solution
   * @return -1 if the first solution dominates the second one, 1 if the second one dominates the
   *     first one, and 0 otherwise
   */
  static int constrainedDominance(DoubleSolution solution1, DoubleSolution solution2) {
    double violation1 = ToP.constraintViolation(solution1);
    double violation2 = ToP.constraintViolation(solution2);
    if (violation1 == 0.0 && violation2 == 0.0) {
      return PARETO_DOMINANCE.compare(solution1, solution2);
    }
    if (violation1 < violation2) {
      return -1;
    }
    return violation1 > violation2 ? 1 : 0;
  }

  /**
   * Returns the front number of each solution, by non-dominated sorting with the constrained
   * dominance.
   *
   * @param solutions the solutions
   * @return the front number of each solution, starting at zero
   */
  static int[] frontNumbers(List<DoubleSolution> solutions) {
    Ranking<DoubleSolution> ranking =
        new FastNonDominatedSortRanking<DoubleSolution>(ToPSelection::constrainedDominance)
            .compute(solutions);
    return solutions.stream().mapToInt(ranking::getRank).toArray();
  }

  /**
   * Returns the crowding distance of each solution within its front. For each objective, the
   * solutions of the front are sorted by that objective from their order in the list (ties keep
   * that order); the first and last ones get an infinite distance, and each inner one adds the
   * difference between the objective values of its neighbors divided by the range of the
   * objective in the front.
   *
   * @param solutions the solutions
   * @param frontNumbers the front number of each solution
   * @return the crowding distance of each solution
   */
  static double[] crowdingDistances(List<DoubleSolution> solutions, int[] frontNumbers) {
    double[] distances = new double[solutions.size()];
    int numberOfFronts = 0;
    for (int frontNumber : frontNumbers) {
      numberOfFronts = Math.max(numberOfFronts, frontNumber + 1);
    }

    for (int front = 0; front < numberOfFronts; front++) {
      List<Integer> members = new ArrayList<>();
      for (int i = 0; i < frontNumbers.length; i++) {
        if (frontNumbers[i] == front) {
          members.add(i);
        }
      }
      if (!members.isEmpty()) {
        addCrowdingDistances(solutions, members, distances);
      }
    }
    return distances;
  }

  private static void addCrowdingDistances(
      List<DoubleSolution> solutions, List<Integer> front, double[] distances) {
    int numberOfObjectives = solutions.get(front.get(0)).objectives().length;
    for (int objective = 0; objective < numberOfObjectives; objective++) {
      int m = objective;
      List<Integer> sorted = new ArrayList<>(front);
      sorted.sort((a, b) -> compareAscending(value(solutions, a, m), value(solutions, b, m)));

      int last = sorted.size() - 1;
      double minimum = value(solutions, sorted.get(0), m);
      double maximum = value(solutions, sorted.get(last), m);
      distances[sorted.get(0)] = Double.POSITIVE_INFINITY;
      distances[sorted.get(last)] = Double.POSITIVE_INFINITY;
      for (int j = 1; j < last; j++) {
        distances[sorted.get(j)] +=
            (value(solutions, sorted.get(j + 1), m) - value(solutions, sorted.get(j - 1), m))
                / (maximum - minimum);
      }
    }
  }

  /**
   * The environmental selection of NSGA-II: the solutions of the best fronts and, from the last
   * front that does not fit, those with the largest crowding distances (NaN first, ties in the
   * order of the list).
   *
   * @param solutions the solutions, usually the union of the population and the offspring
   * @param size the number of solutions to select, not greater than the number of solutions
   * @return a new list with the selected solutions, in the order of the given list
   */
  static List<DoubleSolution> select(List<DoubleSolution> solutions, int size) {
    Check.valueIsInRange(size, 0, solutions.size(), "size");
    int[] frontNumbers = frontNumbers(solutions);
    double[] crowdingDistances = crowdingDistances(solutions, frontNumbers);

    int numberOfFronts = 0;
    for (int frontNumber : frontNumbers) {
      numberOfFronts = Math.max(numberOfFronts, frontNumber + 1);
    }
    int[] frontSizes = new int[numberOfFronts + 1];
    for (int frontNumber : frontNumbers) {
      frontSizes[frontNumber]++;
    }

    int lastFront = 0;
    int selectedBefore = 0;
    while (selectedBefore + frontSizes[lastFront] < size) {
      selectedBefore += frontSizes[lastFront];
      lastFront++;
    }

    boolean[] selected = new boolean[solutions.size()];
    List<Integer> lastFrontMembers = new ArrayList<>();
    for (int i = 0; i < solutions.size(); i++) {
      if (frontNumbers[i] < lastFront) {
        selected[i] = true;
      } else if (frontNumbers[i] == lastFront) {
        lastFrontMembers.add(i);
      }
    }
    lastFrontMembers.sort(
        (a, b) -> compareForTruncation(crowdingDistances[a], crowdingDistances[b]));
    for (int j = 0; j < size - selectedBefore; j++) {
      selected[lastFrontMembers.get(j)] = true;
    }

    List<DoubleSolution> result = new ArrayList<>(size);
    for (int i = 0; i < solutions.size(); i++) {
      if (selected[i]) {
        result.add(solutions.get(i));
      }
    }
    return result;
  }

  /**
   * The binary tournament of PlatEMO's {@code TournamentSelection(2, ...)} on the front number and
   * the crowding distance: the solution with the lower front number wins; with the same front
   * number, the one with the larger crowding distance (a NaN distance loses); with the same
   * distance too, the one with the lower index.
   *
   * @param first the index of a candidate
   * @param second the index of the other candidate, which can be the same
   * @param frontNumbers the front number of each solution
   * @param crowdingDistances the crowding distance of each solution
   * @return the index of the winner
   */
  static int binaryTournament(
      int first, int second, int[] frontNumbers, double[] crowdingDistances) {
    Comparator<Integer> byRank =
        Comparator.<Integer>comparingInt(i -> frontNumbers[i])
            .thenComparing(
                (a, b) -> compareForTournament(crowdingDistances[a], crowdingDistances[b]))
            .thenComparingInt(i -> i);
    return byRank.compare(first, second) <= 0 ? first : second;
  }

  /** Ascending order of values that are not NaN, with -0.0 equal to 0.0 as in MATLAB. */
  private static int compareAscending(double a, double b) {
    if (a < b) {
      return -1;
    }
    return a > b ? 1 : 0;
  }

  /** Descending order with NaN first, as MATLAB's {@code sort(..., 'descend')}. */
  private static int compareForTruncation(double a, double b) {
    if (Double.isNaN(a) || Double.isNaN(b)) {
      return Boolean.compare(Double.isNaN(b), Double.isNaN(a));
    }
    return compareAscending(b, a);
  }

  /** Descending order with NaN last, as MATLAB's ascending {@code sortrows} of the negation. */
  private static int compareForTournament(double a, double b) {
    if (Double.isNaN(a) || Double.isNaN(b)) {
      return Boolean.compare(Double.isNaN(a), Double.isNaN(b));
    }
    return compareAscending(b, a);
  }

  private static double value(List<DoubleSolution> solutions, int index, int objective) {
    return solutions.get(index).objectives()[objective];
  }
}
