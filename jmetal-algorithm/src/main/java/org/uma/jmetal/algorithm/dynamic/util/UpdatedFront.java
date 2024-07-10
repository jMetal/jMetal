package org.uma.jmetal.algorithm.dynamic.util;

import java.util.List;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;

/**
 * Class that keep a front updated according to a quality indicator and a threshold value. When a
 * new front is checked, if the quality indicator value of that front is higher than the value
 * of the current one by considering the threshold, then the current front is updated.
 * @param <S>
 */
public class UpdatedFront<S extends Solution<?>> {

  private final double indicatorThreshold;
  private double lastIndicatorValue;
  private final QualityIndicator indicator;

  private List<S> lastFront;

  public UpdatedFront(double indicatorThresholdValue, QualityIndicator indicator) {
    this.indicatorThreshold = indicatorThresholdValue;
    this.indicator = indicator;
    this.lastIndicatorValue = 0;
    this.lastFront = null ;
  }

  public boolean update(List<S> newValue) {
    double frontIndicatorValue = this.indicator.compute(SolutionListUtils.getMatrixWithObjectiveValues(newValue));
    double aux = Math.abs(frontIndicatorValue - lastIndicatorValue);
    lastIndicatorValue = frontIndicatorValue;
    boolean updateFront = aux > indicatorThreshold;
    if (updateFront) {
      lastFront = newValue;
    }

    return updateFront;
  }

  public List<S> front() {
    return lastFront ;
  }

  public QualityIndicator indicator() {
    return indicator;
  }
}
