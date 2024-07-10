package org.uma.jmetal.util;

import java.util.List;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;

/**
 * The {@code DynamicFrontManager} class maintains a current front (list of solutions)
 * and updates it based on a specified quality indicator and a threshold value.
 * When a new front is checked, if the difference between the quality indicator
 * value of that front and the value of the current one is higher than a threshold,
 * then the current front is updated.
 *
 * @param <S> the type of solutions in the front, extending {@link Solution}
 */
public class DynamicFrontManager<S extends Solution<?>> {

  private final double indicatorThreshold;
  private final QualityIndicator indicator;

  private double currentIndicatorValue;
  private List<S> currentFront;

  /**
   * Constructs an {@code DynamicFrontManager} with the specified indicator threshold value
   * and quality indicator.
   *
   * @param indicatorThresholdValue the threshold value for updating the front
   * @param indicator               the quality indicator used to evaluate the fronts
   */
  public DynamicFrontManager(double indicatorThresholdValue, QualityIndicator indicator) {
    this.indicatorThreshold = indicatorThresholdValue;
    this.indicator = indicator;
    this.currentIndicatorValue = 0;
    this.currentFront = null;
  }

  /**
   * Updates the current front if the quality indicator value of the new front
   * differs from the current one by more than the threshold value.
   *
   * @param newFront the new front to be checked and potentially set as the current front
   * @return {@code true} if the current front was updated, {@code false} otherwise
   */
  public boolean update(List<S> newFront) {
    double frontIndicatorValue = this.indicator.compute(SolutionListUtils.getMatrixWithObjectiveValues(newFront));
    double aux = Math.abs(frontIndicatorValue - currentIndicatorValue);
    boolean updateFront = aux > indicatorThreshold;
    if (updateFront) {
      currentFront = newFront;
      currentIndicatorValue = frontIndicatorValue;
    }

    return updateFront;
  }

  /**
   * Returns the current front.
   *
   * @return the current front, or {@code null} if no front has been set
   */
  public List<S> front() {
    return currentFront;
  }

  /**
   * Returns the quality indicator used by this {@code DynamicFrontManager}.
   *
   * @return the quality indicator
   */
  public QualityIndicator indicator() {
    return indicator;
  }
}
