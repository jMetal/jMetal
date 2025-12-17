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
 * Stochastic Universal Sampling (SUS) is a selection method that provides more uniform
 * selection than standard roulette wheel. Instead of spinning the wheel N times with
 * independent random values, SUS uses N equally-spaced pointers on a single spin.
 *
 * <p>This reduces selection variance and bias, ensuring a more representative sample
 * of the population while still respecting fitness-proportionate probabilities.
 *
 * <p>Uses linear ranking to assign fitness values (similar to {@link RankingSelection}).
 *
 * <p>Control parameters: None. The selection pressure is determined by the linear ranking
 * scheme and cannot be adjusted independently. For adjustable selection pressure,
 * consider {@link BoltzmannSelection}.
 *
 * <p>The matingPoolSize is not a control parameter; it is determined by the variation
 * component based on the offspring population size.
 *
 * @author Antonio J. Nebro
 * @param <S> Type of the solutions
 */
public class StochasticUniversalSampling<S extends Solution<?>> implements Selection<S> {

  private final int matingPoolSize;
  private final Comparator<S> comparator;
  private RankingAndDensityEstimatorPreference<S> preference;

  /**
   * Constructor using a comparator for solution ordering.
   *
   * @param matingPoolSize Number of solutions to select (must be >= 1)
   * @param comparator Comparator to rank solutions
   */
  public StochasticUniversalSampling(int matingPoolSize, Comparator<S> comparator) {
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
  public StochasticUniversalSampling(int matingPoolSize, RankingAndDensityEstimatorPreference<S> preference) {
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
    double[] cumulativeFitness = computeCumulativeFitness(n);
    double totalFitness = cumulativeFitness[n - 1];

    // Distance between pointers
    double pointerDistance = totalFitness / matingPoolSize;
    // Random start point in [0, pointerDistance)
    double start = JMetalRandom.getInstance().nextDouble(0, pointerDistance);

    List<S> matingPool = new ArrayList<>(matingPoolSize);
    int currentIndex = 0;

    for (int i = 0; i < matingPoolSize; i++) {
      double pointer = start + i * pointerDistance;

      // Advance index until cumulative fitness exceeds pointer
      while (cumulativeFitness[currentIndex] < pointer) {
        currentIndex++;
      }

      matingPool.add(sortedList.get(currentIndex));
    }

    return matingPool;
  }

  /**
   * Computes cumulative fitness using linear ranking.
   * Fitness for rank i (0-indexed, 0 is best) = (n - i)
   */
  private double[] computeCumulativeFitness(int n) {
    double[] cumulative = new double[n];
    double sum = 0.0;

    for (int i = 0; i < n; i++) {
      sum += (n - i);
      cumulative[i] = sum;
    }

    return cumulative;
  }

  public int getMatingPoolSize() {
    return matingPoolSize;
  }
}
