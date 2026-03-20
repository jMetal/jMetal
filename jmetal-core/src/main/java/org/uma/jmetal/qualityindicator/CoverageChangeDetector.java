package org.uma.jmetal.qualityindicator;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;

/**
 * Utility class to detect coverage changes between solution fronts using a
 * {@link QualityIndicator}.
 *
 * <p>Behaviors:
 * - `hasSignificantChange(List<S>)` compares the current coverage value with the
 *   last observed value and returns true when the absolute difference exceeds
 *   the configured threshold.
 * - `exceedsThreshold(List<S>)` returns true when the coverage computed for the
 *   given front is greater than the configured threshold.
 *
 * Note: this class is not thread-safe.
 */
public class CoverageChangeDetector<S extends Solution<?>> {

  private final double coverageValue;
  private double lastCoverageValue;
  private final QualityIndicator indicator;

  private List<S> lastFront;

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
  public boolean hasSignificantChange(List<S> front) {
    if (front == null) {
      throw new IllegalArgumentException("front must not be null");
    }

    double coverage = this.indicator.compute(SolutionListUtils.getMatrixWithObjectiveValues(front));
    double aux = Math.abs(coverage - lastCoverageValue);
    lastCoverageValue = coverage;
    boolean result = aux > coverageValue;
    if (result) {
      lastFront = new ArrayList<>(front);
    } else if (lastFront != null) {
      coverage = this.indicator.compute(SolutionListUtils.getMatrixWithObjectiveValues(lastFront));
      aux = Math.abs(coverage - lastCoverageValue);
      result = aux > coverageValue;
      if (result) {
        lastFront = new ArrayList<>(front);
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
  public boolean exceedsThreshold(List<S> front) {
    if (front == null) {
      throw new IllegalArgumentException("front must not be null");
    }

    double coverage = this.indicator.compute(SolutionListUtils.getMatrixWithObjectiveValues(front));
    boolean result = coverage > coverageValue;
    if (result) {
      lastFront = new ArrayList<>(front);
    } else if (lastFront != null) {
      coverage = this.indicator.compute(SolutionListUtils.getMatrixWithObjectiveValues(lastFront));
      result = coverage > coverageValue;
      if (result) {
        lastFront = new ArrayList<>(front);
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
}

