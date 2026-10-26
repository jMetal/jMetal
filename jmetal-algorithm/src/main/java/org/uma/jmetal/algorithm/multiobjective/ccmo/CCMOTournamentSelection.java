package org.uma.jmetal.algorithm.multiobjective.ccmo;

import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Mating selection of CCMO, as the {@code TournamentSelection(2, n, Fitness)} function of PlatEMO:
 * binary tournaments, with replacement, in which the solution with the lower fitness wins. Since
 * PlatEMO ranks the solutions with a stable sort of their fitness, a tie is won by the solution
 * that comes first in the population.
 */
final class CCMOTournamentSelection {

  private CCMOTournamentSelection() {}

  /**
   * Runs a number of binary tournaments.
   *
   * @param fitness the fitness of the solutions of the population (lower is better)
   * @param numberOfTournaments the number of tournaments (of selected solutions)
   * @return the index of the winner of each tournament
   */
  static int[] execute(double[] fitness, int numberOfTournaments) {
    JMetalRandom random = JMetalRandom.getInstance();
    int[] winners = new int[numberOfTournaments];
    for (int t = 0; t < numberOfTournaments; t++) {
      int first = random.nextInt(0, fitness.length - 1);
      int second = random.nextInt(0, fitness.length - 1);
      winners[t] = winner(first, second, fitness);
    }
    return winners;
  }

  /** Returns the winner of the tournament between two solutions of the population. */
  static int winner(int first, int second, double[] fitness) {
    int comparison = Double.compare(fitness[first], fitness[second]);
    return comparison < 0 || (comparison == 0 && first <= second) ? first : second;
  }
}
