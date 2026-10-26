package org.uma.jmetal.qualityindicator.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * The quality indicator for constrained multiobjective optimization of A. Vodopija, T. Tušar and B.
 * Filipič, "Characterization of Constrained Continuous Multiobjective Optimization Problems: A
 * Performance Space Perspective", IEEE Transactions on Evolutionary Computation 29(1):275-285,
 * 2025 (doi 10.1109/TEVC.2024.3366659), I<sup>CMOP</sup>. It measures at once the approximation of
 * the Pareto front and the satisfaction of the constraints, and any set with a feasible solution
 * is better than any set without one.
 *
 * <p>The objectives are normalized with the ideal and nadir points of the reference front (its
 * minimum and maximum values), so that the region of interest is [0, 1]<sup>M</sup> and the nadir
 * point is (1, ..., 1). For a set A of solutions, with F its feasible solutions:
 *
 * <ul>
 *   <li>I<sup>MOP</sup>(A) is minus the hypervolume of the solutions of A that dominate the nadir
 *       point (reference point (1, ..., 1)) if there is one, or else the smallest Euclidean
 *       distance from A to the region of interest (the indicator of the bi-objective suites of the
 *       COCO platform);
 *   <li>I<sup>CMOP</sup>(A) = min(I<sup>MOP</sup>(A ∩ F), τ*) if A has feasible solutions, and
 *       otherwise the smallest overall constraint violation of A plus τ*.
 * </ul>
 *
 * <p>The threshold τ* (1 by default, as in the paper) separates the two cases: the value is below
 * it once a feasible solution is found. The overall constraint violation of a solution is the sum
 * of the violations of its constraints (jMetal's {@link
 * ConstraintHandling#overallConstraintViolationDegree}, in absolute value). Lower values are
 * better.
 *
 * <p>{@link #compute(double[][])} considers every point of the front feasible; {@link
 * #compute(double[][], double[])} and {@link #compute(List)} take the violations into account.
 */
public class ConstrainedMultiobjectiveIndicator extends QualityIndicator {

  /** The threshold of the paper. */
  public static final double DEFAULT_THRESHOLD = 1.0;

  private final double threshold;
  private double[] idealPoint;
  private double[] nadirPoint;

  /** Creates the indicator with the default threshold and no reference front. */
  public ConstrainedMultiobjectiveIndicator() {
    this(DEFAULT_THRESHOLD);
  }

  /**
   * Creates the indicator with no reference front, which must be set before computing it.
   *
   * @param threshold the threshold τ*, which must be positive
   */
  public ConstrainedMultiobjectiveIndicator(double threshold) {
    Check.that(threshold > 0.0, "The threshold must be positive: " + threshold);
    this.threshold = threshold;
  }

  /**
   * Creates the indicator with the default threshold.
   *
   * @param referenceFront the reference front, whose minimum and maximum values are the ideal and
   *     nadir points that normalize the objectives
   */
  public ConstrainedMultiobjectiveIndicator(double[][] referenceFront) {
    this(referenceFront, DEFAULT_THRESHOLD);
  }

  /**
   * Creates the indicator.
   *
   * @param referenceFront the reference front, whose minimum and maximum values are the ideal and
   *     nadir points that normalize the objectives
   * @param threshold the threshold τ*, which must be positive
   */
  public ConstrainedMultiobjectiveIndicator(double[][] referenceFront, double threshold) {
    this(threshold);
    referenceFront(referenceFront);
  }

  @Override
  public void referenceFront(double[][] referenceFront) {
    Check.notNull(referenceFront);
    Check.that(referenceFront.length > 0, "The reference front is empty");
    super.referenceFront(referenceFront);
    int numberOfObjectives = referenceFront[0].length;
    idealPoint = new double[numberOfObjectives];
    nadirPoint = new double[numberOfObjectives];
    Arrays.fill(idealPoint, Double.POSITIVE_INFINITY);
    Arrays.fill(nadirPoint, Double.NEGATIVE_INFINITY);
    for (double[] point : referenceFront) {
      Check.that(
          point.length == numberOfObjectives,
          "The points of the reference front must have the same number of objectives");
      for (int j = 0; j < numberOfObjectives; j++) {
        idealPoint[j] = Math.min(idealPoint[j], point[j]);
        nadirPoint[j] = Math.max(nadirPoint[j], point[j]);
      }
    }
  }

  /**
   * The threshold τ*.
   *
   * @return the threshold
   */
  public double threshold() {
    return threshold;
  }

  /**
   * Computes the indicator for a front whose points are all feasible: min(I<sup>MOP</sup>, τ*).
   *
   * @param front the objective vectors
   * @return the value of the indicator
   */
  @Override
  public double compute(double[][] front) {
    Check.notNull(front);
    return compute(front, new double[front.length]);
  }

  /**
   * Computes the indicator for a set of solutions given by their objective vectors and their
   * overall constraint violations.
   *
   * @param front the objective vectors
   * @param violations the overall constraint violation of each point, non-negative (0 for a
   *     feasible one)
   * @return the value of the indicator
   */
  public double compute(double[][] front, double[] violations) {
    Check.notNull(front);
    Check.notNull(violations);
    Check.that(front.length > 0, "The front is empty");
    Check.that(
        front.length == violations.length,
        "There must be one violation per point: " + front.length + " vs " + violations.length);
    Check.that(idealPoint != null, "The indicator has no reference front");

    double[][] feasible =
        IntStream.range(0, front.length)
            .filter(i -> violations[i] == 0.0)
            .mapToObj(i -> front[i])
            .toArray(double[][]::new);
    if (feasible.length > 0) {
      return Math.min(unconstrainedValue(normalize(feasible)), threshold);
    }
    double leastViolation = Arrays.stream(violations).min().orElseThrow();
    Check.that(leastViolation >= 0.0, "The violations must be non-negative: " + leastViolation);
    return leastViolation + threshold;
  }

  /**
   * Computes the indicator for a list of solutions, with their objectives and their overall
   * constraint violations.
   *
   * @param solutions the solutions
   * @param <S> the type of the solutions
   * @return the value of the indicator
   */
  public <S extends Solution<?>> double compute(List<S> solutions) {
    Check.notNull(solutions);
    Check.that(!solutions.isEmpty(), "The list of solutions is empty");
    double[][] front = solutions.stream().map(Solution::objectives).toArray(double[][]::new);
    double[] violations =
        solutions.stream()
            .mapToDouble(
                solution ->
                    ConstraintHandling.isFeasible(solution)
                        ? 0.0
                        : Math.abs(ConstraintHandling.overallConstraintViolationDegree(solution)))
            .toArray();
    return compute(front, violations);
  }

  /** I<sup>MOP</sup> of a normalized front: minus its hypervolume, or its distance to [0, 1]^M. */
  private double unconstrainedValue(double[][] normalizedFront) {
    double[][] dominatingTheNadir =
        Arrays.stream(normalizedFront)
            .filter(point -> Arrays.stream(point).allMatch(value -> value < 1.0))
            .toArray(double[][]::new);
    if (dominatingTheNadir.length > 0) {
      double[] referencePoint = new double[nadirPoint.length];
      Arrays.fill(referencePoint, 1.0);
      return -new PISAHypervolume(referencePoint).compute(dominatingTheNadir);
    }
    return Arrays.stream(normalizedFront)
        .mapToDouble(ConstrainedMultiobjectiveIndicator::distanceToTheRegionOfInterest)
        .min()
        .orElseThrow();
  }

  /** The Euclidean distance from a normalized point to the region of interest, [0, 1]^M. */
  private static double distanceToTheRegionOfInterest(double[] point) {
    double sum = 0.0;
    for (double value : point) {
      double excess = value > 1.0 ? value - 1.0 : (value < 0.0 ? -value : 0.0);
      sum += excess * excess;
    }
    return Math.sqrt(sum);
  }

  private double[][] normalize(double[][] front) {
    double[][] normalized = new double[front.length][];
    for (int i = 0; i < front.length; i++) {
      Check.that(
          front[i].length == idealPoint.length,
          "The points of the front must have as many objectives as the reference front");
      normalized[i] = new double[idealPoint.length];
      for (int j = 0; j < idealPoint.length; j++) {
        double range = nadirPoint[j] - idealPoint[j];
        normalized[i][j] = (front[i][j] - idealPoint[j]) / (range > 0.0 ? range : 1.0);
      }
    }
    return normalized;
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true;
  }

  @Override
  public QualityIndicator newInstance() {
    return new ConstrainedMultiobjectiveIndicator(threshold);
  }

  @Override
  public String name() {
    return "ICMOP";
  }

  @Override
  public String description() {
    return "Quality indicator for constrained multiobjective optimization (Vodopija et al.)";
  }
}
