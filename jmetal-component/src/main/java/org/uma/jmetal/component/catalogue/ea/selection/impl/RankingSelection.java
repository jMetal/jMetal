package org.uma.jmetal.component.catalogue.ea.selection.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.util.RankingAndDensityEstimatorPreference;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Ranking-based selection (also known as linear ranking selection) assigns selection
 * probabilities based on the rank position of solutions rather than their raw fitness.
 * Solutions are sorted by a comparator, and selection probability is proportional to
 * (N - rank + 1), giving better solutions higher probability of being selected.
 *
 * <p>This provides softer selection pressure than tournament selection and is useful
 * as an alternative in automatic algorithm configuration.
 *
 * <p>Control parameters: None. This operator uses linear ranking with fixed selection pressure.
 * The probability distribution is determined by the population size and cannot be tuned
 * independently. For adjustable selection pressure, consider {@link BoltzmannSelection}.
 *
 * <p>The matingPoolSize is not a control parameter; it is determined by the variation
 * component based on the offspring population size.
 *
 * @author Antonio J. Nebro
 * @param <S> Type of the solutions
 */
public class RankingSelection<S extends Solution<?>> implements Selection<S> {

  private final int matingPoolSize;
  private final Comparator<S> comparator;
  private RankingAndDensityEstimatorPreference<S> preference;

  /**
   * Constructor using a comparator for solution ordering.
   *
   * @param matingPoolSize Number of solutions to select (must be >= 1)
   * @param comparator Comparator to rank solutions
   */
  public RankingSelection(int matingPoolSize, Comparator<S> comparator) {
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
  public RankingSelection(int matingPoolSize, RankingAndDensityEstimatorPreference<S> preference) {
    this.matingPoolSize = matingPoolSize;
    this.preference = preference;
    this.comparator = preference.getComparator();
  }

  @Override
  public List<S> select(List<S> solutionList) {
    Check.notNull(solutionList);
    Check.collectionIsNotEmpty(solutionList);

    if (preference != null) {
      preference.recompute(solutionList);
    }

    List<S> sortedList = new ArrayList<>(solutionList);
    sortedList.sort(comparator);

    int n = sortedList.size();
    double[] cumulativeProbabilities = computeCumulativeProbabilities(n);

    List<S> matingPool = new ArrayList<>(matingPoolSize);
    JMetalRandom random = JMetalRandom.getInstance();

    for (int i = 0; i < matingPoolSize; i++) {
      double r = random.nextDouble();
      int selectedIndex = selectByRoulette(cumulativeProbabilities, r);
      matingPool.add(sortedList.get(selectedIndex));
    }

    return matingPool;
  }

  /**
   * Computes cumulative probabilities using linear ranking.
   * Probability for rank i (0-indexed, 0 is best) = (n - i) / sum(1..n)
   */
  private double[] computeCumulativeProbabilities(int n) {
    double totalRankSum = (n * (n + 1.0)) / 2.0;
    double[] cumulative = new double[n];
    double sum = 0.0;

    for (int i = 0; i < n; i++) {
      double probability = (n - i) / totalRankSum;
      sum += probability;
      cumulative[i] = sum;
    }

    cumulative[n - 1] = 1.0;
    return cumulative;
  }

  /**
   * Selects an index using roulette wheel selection on cumulative probabilities.
   */
  private int selectByRoulette(double[] cumulativeProbabilities, double randomValue) {
    for (int i = 0; i < cumulativeProbabilities.length; i++) {
      if (randomValue <= cumulativeProbabilities[i]) {
        return i;
      }
    }
    return cumulativeProbabilities.length - 1;
  }

  public int getMatingPoolSize() {
    return matingPoolSize;
  }
}
