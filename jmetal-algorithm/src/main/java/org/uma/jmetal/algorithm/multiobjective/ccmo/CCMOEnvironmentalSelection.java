package org.uma.jmetal.algorithm.multiobjective.ccmo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.solution.Solution;

/**
 * Environmental selection of CCMO, as in the {@code EnvironmentalSelection} function of the PlatEMO
 * implementation: the environmental selection of SPEA2 with the fitness of {@link CCMOFitness}.
 *
 * <p>The solutions with a fitness lower than 1 (those that no other solution dominates) are
 * selected. If they are fewer than the population size, the population is filled with the solutions
 * of lowest fitness; if they are more, the truncation of SPEA2 removes, one at a time, the solution
 * whose distances to the remaining ones, sorted in ascending order, are the lexicographically
 * smallest. The selected solutions are returned sorted by their fitness, which is the one computed
 * on the whole list. All the ties (in the sorts and in the truncation) are broken in favour of the
 * solution that comes first in the list, as the stable sorts of MATLAB do.
 */
final class CCMOEnvironmentalSelection {

  /**
   * The selected solutions, sorted by fitness, and their fitness.
   *
   * @param population the selected solutions
   * @param fitness the fitness of each selected solution, in the order of the population
   */
  record Result<S>(List<S> population, double[] fitness) {}

  private CCMOEnvironmentalSelection() {}

  /**
   * Selects {@code populationSize} solutions from a list.
   *
   * @param solutions the list of evaluated solutions (at least {@code populationSize})
   * @param populationSize the number of solutions to select
   * @param withConstraints whether the selection is for the original problem (the constraint
   *     violation is taken into account) or for the helper problem (it is ignored)
   * @return the selected solutions, sorted by fitness, and their fitness
   */
  static <S extends Solution<?>> Result<S> execute(
      List<S> solutions, int populationSize, boolean withConstraints) {
    double[] fitness = CCMOFitness.compute(solutions, withConstraints);
    int[] nonDominated =
        IntStream.range(0, solutions.size()).filter(i -> fitness[i] < 1.0).toArray();

    boolean[] selected = new boolean[solutions.size()];
    for (int i : nonDominated) {
      selected[i] = true;
    }
    if (nonDominated.length < populationSize) {
      int[] sorted = sortedByFitness(IntStream.range(0, solutions.size()).toArray(), fitness);
      for (int i = 0; i < populationSize; i++) {
        selected[sorted[i]] = true;
      }
    } else if (nonDominated.length > populationSize) {
      double[][] objectives =
          Arrays.stream(nonDominated)
              .mapToObj(i -> solutions.get(i).objectives())
              .toArray(double[][]::new);
      boolean[] deleted = truncation(objectives, nonDominated.length - populationSize);
      for (int i = 0; i < nonDominated.length; i++) {
        selected[nonDominated[i]] = !deleted[i];
      }
    }

    int[] next =
        sortedByFitness(
            IntStream.range(0, solutions.size()).filter(i -> selected[i]).toArray(), fitness);
    List<S> population = new ArrayList<>(next.length);
    double[] nextFitness = new double[next.length];
    for (int i = 0; i < next.length; i++) {
      population.add(solutions.get(next[i]));
      nextFitness[i] = fitness[next[i]];
    }
    return new Result<>(population, nextFitness);
  }

  /**
   * Truncation of SPEA2, as the {@code Truncation} function of PlatEMO: removes, one at a time, the
   * vector whose distances to the remaining ones, sorted in ascending order, are the
   * lexicographically smallest (the first one in the array in case of a tie).
   *
   * @param objectives the objective vectors (one per row)
   * @param numberToDelete the number of vectors to remove
   * @return whether each vector is removed
   */
  static boolean[] truncation(double[][] objectives, int numberToDelete) {
    int n = objectives.length;
    double[][] distances = CCMOFitness.distances(objectives);
    int[][] neighbours = new int[n][];
    for (int i = 0; i < n; i++) {
      double[] row = distances[i];
      int self = i;
      neighbours[i] =
          IntStream.range(0, n)
              .filter(j -> j != self)
              .boxed()
              .sorted(Comparator.comparingDouble(j -> row[j]))
              .mapToInt(Integer::intValue)
              .toArray();
    }

    boolean[] deleted = new boolean[n];
    for (int removed = 0; removed < numberToDelete; removed++) {
      int candidate = -1;
      for (int i = 0; i < n; i++) {
        if (!deleted[i]
            && (candidate < 0
                || compareSortedDistances(i, candidate, distances, neighbours, deleted) < 0)) {
          candidate = i;
        }
      }
      deleted[candidate] = true;
    }
    return deleted;
  }

  /**
   * Compares lexicographically the sorted distances of two vectors to the remaining ones. Both
   * lists have the same length (the number of remaining vectors minus one).
   */
  private static int compareSortedDistances(
      int a, int b, double[][] distances, int[][] neighbours, boolean[] deleted) {
    int positionA = nextRemaining(neighbours[a], 0, deleted);
    int positionB = nextRemaining(neighbours[b], 0, deleted);
    int comparison = 0;
    while (comparison == 0 && positionA < neighbours[a].length) {
      double distanceA = distances[a][neighbours[a][positionA]];
      double distanceB = distances[b][neighbours[b][positionB]];
      if (distanceA < distanceB) {
        comparison = -1;
      } else if (distanceA > distanceB) {
        comparison = 1;
      }
      positionA = nextRemaining(neighbours[a], positionA + 1, deleted);
      positionB = nextRemaining(neighbours[b], positionB + 1, deleted);
    }
    return comparison;
  }

  private static int nextRemaining(int[] neighbours, int position, boolean[] deleted) {
    int next = position;
    while (next < neighbours.length && deleted[neighbours[next]]) {
      next++;
    }
    return next;
  }

  /** Returns the indices sorted by fitness; the sort is stable. */
  private static int[] sortedByFitness(int[] indices, double[] fitness) {
    return Arrays.stream(indices)
        .boxed()
        .sorted(Comparator.comparingDouble(i -> fitness[i]))
        .mapToInt(Integer::intValue)
        .toArray();
  }
}
