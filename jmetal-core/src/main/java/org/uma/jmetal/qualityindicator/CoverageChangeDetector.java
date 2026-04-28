package org.uma.jmetal.qualityindicator;

import org.uma.jmetal.qualityindicator.QualityIndicator;

/**
 * Detects meaningful changes in coverage values computed over solution fronts.
 *
 * <p>This class wraps a {@link QualityIndicator} and provides two convenience
 * predicates that operate on numeric fronts represented as a matrix of double
 * values {@code double[][]} (each row is a solution objective vector):
 *
 * <ul>
 *   <li>{@link #hasSignificantChange(double[][])} — returns {@code true} when the
 *       absolute difference between the coverage computed for the provided
 *       front and the last observed coverage is greater than the configured
 *       threshold.</li>
 *   <li>{@link #exceedsThreshold(double[][])} — returns {@code true} when the
 *       coverage computed for the provided front is greater than the configured
 *       threshold.</li>
 * </ul>
 *
 * <p>The detector stores the last evaluated front (defensively copied) and the
 * last coverage value to support comparisons. The underlying
 * {@link QualityIndicator} is used directly to compute coverage values and to
 * update the reference front via {@link #updateReferenceFront(double[][])}.
 *
 * <p>Usage example:
 * <pre>
 * CoverageChangeDetector detector = new CoverageChangeDetector(0.05, igd);
 * double[][] front = SolutionListUtils.getMatrixWithObjectiveValues(solutions);
 * if (detector.hasSignificantChange(front)) { ... }
 * </pre>
 *
 * <p>Thread-safety: this class is not thread-safe. Callers must synchronize
 * externally if accessed from multiple threads.
 */
public class CoverageChangeDetector {

  private final double coverageValue;
  private double lastCoverageValue;
  private final QualityIndicator indicator;

  private double[][] lastFront;

  /**
   * Create a detector with a coverage threshold and a quality indicator.
   *
   * @param coverageValue threshold used to determine significant changes
   * @param indicator quality indicator used to compute coverage values (must not be null)
   * @throws IllegalArgumentException if {@code indicator} is null
   */
  public CoverageChangeDetector(double coverageValue, QualityIndicator indicator) {
    if (indicator == null) {
      throw new IllegalArgumentException("indicator must not be null");
    }
    this.coverageValue = coverageValue;
    this.indicator = indicator;
    this.lastCoverageValue = 0;
  }

  /**
   * Compute coverage for the given front and check whether the absolute change
   * with respect to the last observed coverage exceeds the configured threshold.
   *
   * @param front list of solutions (must not be null)
   * @return true if a significant change was detected
   * @throws IllegalArgumentException if {@code front} is null
   */
  public boolean hasSignificantChange(double[][] front) {
    if (front == null) {
      throw new IllegalArgumentException("front must not be null");
    }

    double coverage = this.indicator.compute(front);
    double aux = Math.abs(coverage - lastCoverageValue);
    lastCoverageValue = coverage;
    boolean result = aux > coverageValue;
    if (result) {
      lastFront = copyMatrix(front);
    } else if (lastFront != null) {
      coverage = this.indicator.compute(lastFront);
      aux = Math.abs(coverage - lastCoverageValue);
      result = aux > coverageValue;
      if (result) {
        lastFront = copyMatrix(front);
      }
    }
    return result;
  }

  /**
   * Returns true when the coverage computed for {@code front} exceeds the
   * configured threshold.
   *
   * @param front list of solutions (must not be null)
   * @return true if coverage &gt; threshold
   * @throws IllegalArgumentException if {@code front} is null
   */
  public boolean exceedsThreshold(double[][] front) {
    if (front == null) {
      throw new IllegalArgumentException("front must not be null");
    }

    double coverage = this.indicator.compute(front);
    boolean result = coverage > coverageValue;
    if (result) {
      lastFront = copyMatrix(front);
    } else if (lastFront != null) {
      coverage = this.indicator.compute(lastFront);
      result = coverage > coverageValue;
      if (result) {
        lastFront = copyMatrix(front);
      }
    }
    return result;
  }

  /**
   * Update the reference front used by the underlying quality indicator.
   *
   * @param front matrix representing the reference front (may be null)
   */
  public void updateReferenceFront(double[][] front) {
    try {
      this.indicator.referenceFront(front);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private static double[][] copyMatrix(double[][] matrix) {
    if (matrix == null) {
      return null;
    }
    double[][] copy = new double[matrix.length][];
    for (int i = 0; i < matrix.length; i++) {
      copy[i] = matrix[i] == null ? null : matrix[i].clone();
    }
    return copy;
  }
}

