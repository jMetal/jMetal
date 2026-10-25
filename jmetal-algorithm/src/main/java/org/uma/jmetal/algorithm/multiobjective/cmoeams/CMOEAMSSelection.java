package org.uma.jmetal.algorithm.multiobjective.cmoeams;

import java.util.Arrays;
import java.util.stream.IntStream;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;

/**
 * Selections of CMOEA-MS: the environmental selection of PlatEMO's {@code
 * EnvironmentalSelection.m}, with its truncation by cosine distance, and the binary tournament of
 * PlatEMO's {@code TournamentSelection(2, N, Fitness)}.
 */
final class CMOEAMSSelection {
  private CMOEAMSSelection() {}

  /**
   * Selects {@code n} solutions as {@code EnvironmentalSelection.m}. The solutions whose fitness
   * is lower than 1 (the non-dominated ones) are selected; if they are fewer than {@code n}, the
   * {@code n} solutions with the lowest fitness are selected (ties keep the order of the
   * solutions, and a NaN fitness goes last); if they are more, the excess is removed by {@link
   * #truncation} on their objectives. The selected solutions keep their order.
   *
   * @param fitness the fitness of the solutions
   * @param objectives the objective values of the solutions (one row per solution), used by the
   *     truncation
   * @param n the number of solutions to select
   * @return the indices of the selected solutions, in ascending order
   */
  static int[] environmentalSelection(double[] fitness, double[][] objectives, int n) {
    Check.notNull(fitness, "fitness");
    Check.notNull(objectives, "objectives");
    Check.that(
        fitness.length == objectives.length,
        "The fitness and the objectives have different sizes");
    Check.valueIsInRange(n, 0, fitness.length, "n");

    boolean[] next = new boolean[fitness.length];
    int count = 0;
    for (int i = 0; i < fitness.length; i++) {
      next[i] = fitness[i] < 1.0;
      count += next[i] ? 1 : 0;
    }

    if (count < n) {
      int[] rank = CMOEAMSFitness.sortedIndices(fitness);
      for (int p = 0; p < n; p++) {
        next[rank[p]] = true;
      }
    } else if (count > n) {
      int[] candidates = IntStream.range(0, fitness.length).filter(i -> next[i]).toArray();
      double[][] candidateObjectives =
          Arrays.stream(candidates).mapToObj(i -> objectives[i]).toArray(double[][]::new);
      boolean[] deleted = truncation(candidateObjectives, count - n);
      for (int k = 0; k < candidates.length; k++) {
        if (deleted[k]) {
          next[candidates[k]] = false;
        }
      }
    }

    return IntStream.range(0, fitness.length).filter(i -> next[i]).toArray();
  }

  /**
   * Removes {@code k} solutions one by one as the {@code Truncation} function of {@code
   * EnvironmentalSelection.m}: the distances between the remaining solutions (the cosine distance
   * between their objective vectors) are sorted for each solution, and the solution whose sorted
   * distances are the lexicographically smallest is removed (the first one in case of a tie).
   *
   * @param objectives the objective values of the solutions (one row per solution)
   * @param k the number of solutions to remove
   * @return a flag for each solution that is true if the solution is removed
   */
  static boolean[] truncation(double[][] objectives, int k) {
    Check.notNull(objectives, "objectives");
    int numberOfSolutions = objectives.length;
    Check.valueIsInRange(k, 0, numberOfSolutions, "k");

    double[][] distance = CMOEAMSFitness.cosineDistances(objectives);
    double[][] sorted = new double[numberOfSolutions][];
    for (int i = 0; i < numberOfSolutions; i++) {
      distance[i][i] = Double.POSITIVE_INFINITY;
      sorted[i] = distance[i].clone();
      Arrays.sort(sorted[i]);
    }

    boolean[] deleted = new boolean[numberOfSolutions];
    int length = numberOfSolutions;
    for (int removed = 0; removed < k; removed++) {
      int worst = -1;
      for (int i = 0; i < numberOfSolutions; i++) {
        if (!deleted[i]
            && (worst < 0 || compareLexicographically(sorted[i], sorted[worst], length) < 0)) {
          worst = i;
        }
      }
      deleted[worst] = true;

      // The sorted distances of the remaining solutions lose their distance to the removed one
      for (int i = 0; i < numberOfSolutions; i++) {
        if (!deleted[i]) {
          int position = Arrays.binarySearch(sorted[i], 0, length, distance[i][worst]);
          System.arraycopy(sorted[i], position + 1, sorted[i], position, length - position - 1);
        }
      }
      length--;
    }

    return deleted;
  }

  /**
   * Selects {@code count} solutions by binary tournaments on the fitness, as {@code
   * TournamentSelection(2, count, fitness)} of PlatEMO: each tournament draws two solutions at
   * random (with replacement) and selects the one ranked first when the fitness values are sorted
   * in ascending order (ties keep the order of the solutions, and a NaN fitness goes last).
   *
   * @param fitness the fitness of the solutions
   * @param count the number of tournaments
   * @param random a generator of integers in a closed range
   * @return the indices of the selected solutions
   */
  static int[] tournamentSelection(
      double[] fitness, int count, BoundedRandomGenerator<Integer> random) {
    Check.notNull(fitness, "fitness");
    Check.notNull(random, "random");
    Check.that(fitness.length > 0, "There are no solutions to select");

    int[] order = CMOEAMSFitness.sortedIndices(fitness);
    int[] rank = new int[fitness.length];
    for (int p = 0; p < order.length; p++) {
      rank[order[p]] = p;
    }

    int[] result = new int[count];
    for (int t = 0; t < count; t++) {
      int first = random.getRandomValue(0, fitness.length - 1);
      int second = random.getRandomValue(0, fitness.length - 1);
      result[t] = rank[second] < rank[first] ? second : first;
    }
    return result;
  }

  private static int compareLexicographically(double[] a, double[] b, int length) {
    for (int p = 0; p < length; p++) {
      int comparison = CMOEAMSFitness.compareAscending(a[p], b[p]);
      if (comparison != 0) {
        return comparison;
      }
    }
    return 0;
  }
}
