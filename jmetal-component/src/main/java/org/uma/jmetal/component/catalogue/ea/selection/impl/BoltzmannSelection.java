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
 * Boltzmann selection uses a temperature-controlled probability distribution inspired
 * by simulated annealing. Selection probability is proportional to exp(fitness / temperature).
 *
 * <p>At high temperatures, selection is nearly uniform (exploration).
 * At low temperatures, selection strongly favors the best solutions (exploitation).
 *
 * <p>Uses linear ranking to assign fitness values: fitness(rank i) = (n - i)
 *
 * <p>Control parameters:
 * <ul>
 *   <li><b>temperature</b>: Controls selection pressure.
 *       Range: (0, +∞). Recommended range for auto-configuration: [0.1, 100.0].
 *       Default: 1.0 (moderate selection pressure).
 *       <ul>
 *         <li>Low values (e.g., 0.1-1.0): High selection pressure, strongly favors best solutions</li>
 *         <li>Medium values (e.g., 1.0-10.0): Moderate selection pressure</li>
 *         <li>High values (e.g., 10.0-100.0): Low selection pressure, nearly uniform selection</li>
 *       </ul>
 *   </li>
 * </ul>
 *
 * <p>The temperature parameter is particularly useful for automatic algorithm configuration
 * as it provides continuous control over selection pressure.
 *
 * <p>The matingPoolSize is not a control parameter; it is determined by the variation
 * component based on the offspring population size.
 *
 * @author Antonio J. Nebro
 * @param <S> Type of the solutions
 */
public class BoltzmannSelection<S extends Solution<?>> implements Selection<S> {

  public static final double DEFAULT_TEMPERATURE = 1.0;

  private final int matingPoolSize;
  private final Comparator<S> comparator;
  private final double temperature;
  private RankingAndDensityEstimatorPreference<S> preference;

  /**
   * Constructor using a comparator with default temperature.
   *
   * @param matingPoolSize Number of solutions to select (must be >= 1)
   * @param comparator Comparator to rank solutions
   */
  public BoltzmannSelection(int matingPoolSize, Comparator<S> comparator) {
    this(matingPoolSize, comparator, DEFAULT_TEMPERATURE);
  }

  /**
   * Constructor using a comparator for solution ordering.
   *
   * @param matingPoolSize Number of solutions to select (must be >= 1)
   * @param comparator Comparator to rank solutions
   * @param temperature Controls selection pressure (must be > 0). Default: 1.0
   */
  public BoltzmannSelection(int matingPoolSize, Comparator<S> comparator, double temperature) {
    Check.that(temperature > 0, "Temperature must be positive");
    this.matingPoolSize = matingPoolSize;
    this.comparator = comparator;
    this.temperature = temperature;
    this.preference = null;
  }

  /**
   * Constructor using ranking and density estimator preference with default temperature.
   *
   * @param matingPoolSize Number of solutions to select (must be >= 1)
   * @param preference Ranking and density estimator preference
   */
  public BoltzmannSelection(int matingPoolSize, RankingAndDensityEstimatorPreference<S> preference) {
    this(matingPoolSize, preference, DEFAULT_TEMPERATURE);
  }

  /**
   * Constructor using ranking and density estimator preference.
   *
   * @param matingPoolSize Number of solutions to select (must be >= 1)
   * @param preference Ranking and density estimator preference
   * @param temperature Controls selection pressure (must be > 0). Default: 1.0
   */
  public BoltzmannSelection(int matingPoolSize, RankingAndDensityEstimatorPreference<S> preference, double temperature) {
    Check.that(temperature > 0, "Temperature must be positive");
    this.matingPoolSize = matingPoolSize;
    this.preference = preference;
    this.comparator = preference.getComparator();
    this.temperature = temperature;
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
    double[] cumulativeProbabilities = computeBoltzmannProbabilities(n);

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
   * Computes Boltzmann probabilities: P(i) = exp(fitness(i) / T) / Z
   * where Z is the partition function (normalization constant).
   * Fitness is based on linear ranking: fitness(rank i) = (n - i)
   */
  private double[] computeBoltzmannProbabilities(int n) {
    double[] boltzmannValues = new double[n];
    double partitionFunction = 0.0;

    // Compute unnormalized Boltzmann values
    for (int i = 0; i < n; i++) {
      double fitness = n - i; // Linear ranking fitness
      boltzmannValues[i] = Math.exp(fitness / temperature);
      partitionFunction += boltzmannValues[i];
    }

    // Compute cumulative probabilities
    double[] cumulative = new double[n];
    double sum = 0.0;
    for (int i = 0; i < n; i++) {
      sum += boltzmannValues[i] / partitionFunction;
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

  public double getTemperature() {
    return temperature;
  }
}
