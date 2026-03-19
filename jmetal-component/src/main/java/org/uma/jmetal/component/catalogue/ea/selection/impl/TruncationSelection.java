package org.uma.jmetal.component.catalogue.ea.selection.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.util.RankingAndDensityEstimatorPreference;
import org.uma.jmetal.solution.Solution;

/**
 * Truncation selection deterministically selects the best solutions from a population
 * according to a comparator or ranking/density preference. This is a greedy, elitist
 * selection strategy with no stochasticity—it always selects the top-ranked solutions.
 *
 * <p>Control parameters: None. This operator has no tunable parameters, making it useful
 * as a deterministic baseline in automatic algorithm configuration.
 *
 * <p>The matingPoolSize is not a control parameter; it is determined by the variation
 * component based on the offspring population size.
 *
 * @author Antonio J. Nebro
 * @param <S> Type of the solutions
 */
public class TruncationSelection<S extends Solution<?>> implements Selection<S> {

  private final int matingPoolSize;
  private final Comparator<S> comparator;
  private RankingAndDensityEstimatorPreference<S> preference;

  /**
   * Constructor using a comparator for solution ordering.
   *
   * @param matingPoolSize Number of solutions to select (must be >= 1)
   * @param comparator Comparator to rank solutions
   */
  public TruncationSelection(int matingPoolSize, Comparator<S> comparator) {
    this.matingPoolSize = matingPoolSize;
    this.comparator = comparator;
    this.preference = null;
  }

  /**
   * Constructor using ranking and density estimator preference.
   *
   * @param matingPoolSize Number of solutions to select (must be >= 1)
   * @param preference Ranking and density estimator preference
   */
  public TruncationSelection(int matingPoolSize, RankingAndDensityEstimatorPreference<S> preference) {
    this.matingPoolSize = matingPoolSize;
    this.preference = preference;
    this.comparator = preference.getComparator();
  }

  @Override
  public List<S> select(List<S> solutionList) {
    if (preference != null) {
      preference.recompute(solutionList);
    }

    List<S> sortedList = new ArrayList<>(solutionList);
    sortedList.sort(comparator);

    int selectionSize = Math.min(matingPoolSize, sortedList.size());
    return new ArrayList<>(sortedList.subList(0, selectionSize));
  }

  public int getMatingPoolSize() {
    return matingPoolSize;
  }
}
