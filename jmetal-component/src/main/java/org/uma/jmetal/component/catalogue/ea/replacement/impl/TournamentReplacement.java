package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ListUtils;
import org.uma.jmetal.util.SolutionListUtils;

/**
 * Tournament-based replacement strategy that selects N solutions from the combined
 * parent and offspring populations using tournament selection, where N is the size
 * of the parent population.
 *
 * <p>This strategy provides configurable selection pressure through the tournament size
 * parameter, making it useful for automatic algorithm configuration.
 *
 * <p>Control parameters:
 * <ul>
 *   <li><b>tournamentSize</b>: Number of solutions competing in each tournament.
 *       Range: [2, populationSize + offspringSize]. Default: 2 (binary tournament).
 *       Higher values increase selection pressure toward better solutions.</li>
 * </ul>
 *
 * @author Antonio J. Nebro
 * @param <S> Type of the solutions
 */
public class TournamentReplacement<S extends Solution<?>> implements Replacement<S> {

  public static final int DEFAULT_TOURNAMENT_SIZE = 2;

  private final int tournamentSize;
  private final Comparator<S> comparator;

  /**
   * Constructor with default tournament size (binary tournament).
   *
   * @param comparator Comparator to determine the winner of each tournament
   */
  public TournamentReplacement(Comparator<S> comparator) {
    this(DEFAULT_TOURNAMENT_SIZE, comparator);
  }

  /**
   * Constructor with configurable tournament size.
   *
   * @param tournamentSize Number of solutions in each tournament (must be >= 2)
   * @param comparator Comparator to determine the winner of each tournament
   */
  public TournamentReplacement(int tournamentSize, Comparator<S> comparator) {
    this.tournamentSize = tournamentSize;
    this.comparator = comparator;
  }

  /**
   * Replaces the current population by selecting solutions from the combined
   * parent and offspring populations using tournament selection.
   *
   * @param population Current population of size N
   * @param offspringPopulation Offspring population
   * @return New population of size N selected via tournaments
   */
  @Override
  public List<S> replace(List<S> population, List<S> offspringPopulation) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population);
    jointPopulation.addAll(offspringPopulation);

    List<S> resultPopulation = new ArrayList<>(population.size());

    while (resultPopulation.size() < population.size()) {
      int effectiveTournamentSize = Math.min(tournamentSize, jointPopulation.size());
      List<S> tournamentParticipants =
          ListUtils.randomSelectionWithoutReplacement(effectiveTournamentSize, jointPopulation);
      S winner = SolutionListUtils.findBestSolution(tournamentParticipants, comparator);
      resultPopulation.add(winner);
    }

    return resultPopulation;
  }

  public int getTournamentSize() {
    return tournamentSize;
  }
}
