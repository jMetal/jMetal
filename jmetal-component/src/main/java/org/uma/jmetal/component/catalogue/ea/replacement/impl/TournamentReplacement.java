package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.util.RankingAndDensityEstimatorPreference;
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
 * <p>The comparison between solutions uses Pareto ranking and density estimation
 * (e.g., crowding distance), which is computed on the joint population before
 * tournament selection begins.
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
  private final RankingAndDensityEstimatorPreference<S> preference;

  /**
   * Constructor using ranking and density estimator preference with default tournament size.
   *
   * @param preference Ranking and density estimator preference for multi-objective comparison
   */
  public TournamentReplacement(RankingAndDensityEstimatorPreference<S> preference) {
    this(DEFAULT_TOURNAMENT_SIZE, preference);
  }

  /**
   * Constructor using ranking and density estimator preference with configurable tournament size.
   *
   * @param tournamentSize Number of solutions in each tournament (must be >= 2)
   * @param preference Ranking and density estimator preference for multi-objective comparison
   */
  public TournamentReplacement(int tournamentSize, RankingAndDensityEstimatorPreference<S> preference) {
    this.tournamentSize = tournamentSize;
    this.preference = preference;
    this.comparator = preference.getComparator();
  }

  /**
   * Constructor with explicit comparator and default tournament size (for single-objective or custom comparison).
   *
   * @param comparator Comparator to determine the winner of each tournament
   */
  public TournamentReplacement(Comparator<S> comparator) {
    this(DEFAULT_TOURNAMENT_SIZE, comparator);
  }

  /**
   * Constructor with explicit comparator (for single-objective or custom comparison).
   *
   * @param tournamentSize Number of solutions in each tournament (must be >= 2)
   * @param comparator Comparator to determine the winner of each tournament
   */
  public TournamentReplacement(int tournamentSize, Comparator<S> comparator) {
    this.tournamentSize = tournamentSize;
    this.comparator = comparator;
    this.preference = null;
  }

  /**
   * Replaces the current population by selecting solutions from the combined
   * parent and offspring populations using tournament selection.
   *
   * <p>When using RankingAndDensityEstimatorPreference, the ranking and density
   * values are computed on the joint population before selection begins.
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

    // Compute ranking and density on joint population if using preference
    if (preference != null) {
      preference.recompute(jointPopulation);
    }

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
