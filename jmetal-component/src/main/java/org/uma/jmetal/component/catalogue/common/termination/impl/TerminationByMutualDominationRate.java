package org.uma.jmetal.component.catalogue.common.termination.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Adaptive stopping criterion MGBM (L. Martí, J. García, A. Berlanga and J. M. Molina, "A stopping
 * criterion for multi-objective optimization evolutionary algorithms", Information Sciences 367-368,
 * 2016), which stops the search when the population no longer improves.
 *
 * <p>Each time it is checked, it computes the mutual domination rate (MDR) between the current
 * population P(t) and the one of the previous check P(t-1):
 *
 * <pre>
 *   MDR = |{x in P(t-1) dominated by P(t)}| / |P(t-1)| - |{x in P(t) dominated by P(t-1)}| / |P(t)|
 * </pre>
 *
 * <p>which is 1 when the new population dominates the whole previous one, 0 when neither improves
 * the other, and negative when the population gets worse. The MDR values are noisy, so they are the
 * observations of a one-dimensional Kalman filter, and the search stops when the estimate of the
 * filter falls below a threshold.
 *
 * <p>The default values follow the implementation of K. Kitamura and R. Tanabe ("Benchmarking
 * Stopping Criteria for Evolutionary Multi-objective Optimization", GECCO 2026): an initial estimate
 * of 1, an observation noise of 0.05, which is also the initial covariance, and no process noise.
 * Without process noise the estimate is the mean of the initial estimate and all the MDR values so
 * far, so the criterion stops when the average improvement since the beginning of the run is low.
 * They used a threshold of 0.12, because with the 0.0001 of the original paper the search did not
 * stop within 100,000 evaluations; the threshold has to be tuned for each algorithm and problem. A
 * positive process noise gives more weight to the recent MDR values.
 *
 * <p>The domination is Pareto dominance on the objectives (minimization), which ignores the
 * constraints. The first check only stores the population. The criterion keeps the state of the run,
 * so an instance cannot be used in two runs. It does not bound the number of evaluations, so it
 * should be combined with a budget-based termination.
 */
public class TerminationByMutualDominationRate implements Termination {
  public static final double DEFAULT_OBSERVATION_NOISE = 0.05;
  public static final double DEFAULT_PROCESS_NOISE = 0.0;
  private static final double INITIAL_ESTIMATE = 1.0;

  private final double threshold;
  private final double observationNoise;
  private final double processNoise;

  private double[][] previousFront;
  private double estimate;
  private double covariance;
  private OptionalDouble lastMutualDominationRate = OptionalDouble.empty();

  /**
   * Creates the criterion with the default observation noise and no process noise.
   *
   * @param threshold the search stops when the estimate of the MDR falls below this value
   */
  public TerminationByMutualDominationRate(double threshold) {
    this(threshold, DEFAULT_OBSERVATION_NOISE, DEFAULT_PROCESS_NOISE);
  }

  /**
   * Creates the criterion.
   *
   * @param threshold the search stops when the estimate of the MDR falls below this value, in (0, 1)
   * @param observationNoise the variance of the noise of the MDR values, which is also the initial
   *     covariance of the filter; it must be positive
   * @param processNoise the variance of the change of the MDR between two checks; with 0 the estimate
   *     is the mean of all the MDR values
   */
  public TerminationByMutualDominationRate(
      double threshold, double observationNoise, double processNoise) {
    Check.that(
        threshold > 0.0 && threshold < 1.0, "The threshold must be in (0, 1): " + threshold);
    Check.that(observationNoise > 0.0, "The observation noise must be positive: " + observationNoise);
    Check.valueIsNotNegative(processNoise);

    this.threshold = threshold;
    this.observationNoise = observationNoise;
    this.processNoise = processNoise;
    this.estimate = INITIAL_ESTIMATE;
    this.covariance = observationNoise;
  }

  @Override
  public boolean isMet(Map<String, Object> algorithmStatusData) {
    Check.notNull(algorithmStatusData.get("POPULATION"));
    @SuppressWarnings("unchecked")
    List<Solution<?>> population = (List<Solution<?>>) algorithmStatusData.get("POPULATION");
    Check.collectionIsNotEmpty(population);

    double[][] front = copyOfTheObjectives(population);
    if (previousFront == null) {
      previousFront = front;
      return false;
    }

    double mutualDominationRate = mutualDominationRate(front, previousFront);
    update(mutualDominationRate);
    lastMutualDominationRate = OptionalDouble.of(mutualDominationRate);
    previousFront = front;

    return estimate < threshold;
  }

  private void update(double observation) {
    double priorCovariance = covariance + processNoise;
    double gain = priorCovariance / (priorCovariance + observationNoise);
    estimate = estimate + gain * (observation - estimate);
    covariance = (1.0 - gain) * priorCovariance;
  }

  /**
   * The fraction of the previous front dominated by the current one minus the fraction of the
   * current front dominated by the previous one.
   */
  static double mutualDominationRate(double[][] currentFront, double[][] previousFront) {
    return fractionOfDominatedVectors(previousFront, currentFront)
        - fractionOfDominatedVectors(currentFront, previousFront);
  }

  /** The fraction of the vectors of the target that are dominated by a vector of the reference. */
  private static double fractionOfDominatedVectors(double[][] target, double[][] reference) {
    long dominated =
        Arrays.stream(target)
            .filter(vector -> Arrays.stream(reference).anyMatch(other -> dominates(other, vector)))
            .count();
    return dominated / (double) target.length;
  }

  /** True if the first vector Pareto-dominates the second one (minimization). */
  private static boolean dominates(double[] first, double[] second) {
    boolean strictlyBetterInOne = false;
    for (int i = 0; i < first.length; i++) {
      if (first[i] > second[i]) {
        return false;
      }
      if (first[i] < second[i]) {
        strictlyBetterInOne = true;
      }
    }
    return strictlyBetterInOne;
  }

  private static double[][] copyOfTheObjectives(List<Solution<?>> population) {
    return population.stream()
        .map(solution -> solution.objectives().clone())
        .toArray(double[][]::new);
  }

  /** The current estimate of the MDR, which starts at 1. */
  public double estimate() {
    return estimate;
  }

  /** The MDR of the last check, or an empty value if there has not been a comparison yet. */
  public OptionalDouble lastMutualDominationRate() {
    return lastMutualDominationRate;
  }

  public double threshold() {
    return threshold;
  }

  public double observationNoise() {
    return observationNoise;
  }

  public double processNoise() {
    return processNoise;
  }
}
