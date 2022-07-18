package org.uma.jmetal.algorithm.multiobjective.nsgaii.util;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;

public class CoverageFront<S extends Solution<?>> {

  private double coverageValue;
  private double lastCoverageValue;
  private QualityIndicator indicator;

  private List<S> lastFront;

  public CoverageFront(double coverageValue, QualityIndicator indicator) {
    this.coverageValue = coverageValue;
    this.indicator = indicator;
    this.lastCoverageValue = 0;
  }

  public boolean isCoverageWithLast(@NotNull List<S> front) {
    var coverage = this.indicator.compute(SolutionListUtils.getMatrixWithObjectiveValues(front));
    var aux = Math.abs(coverage - lastCoverageValue);
    lastCoverageValue = coverage;
    var result = aux > coverageValue;
    if (result) {
      lastFront = front;
    } else if (lastFront != null) {
      coverage = this.indicator.compute(SolutionListUtils.getMatrixWithObjectiveValues(lastFront));
      aux = Math.abs(coverage - lastCoverageValue);
      result = aux > coverageValue;
      if (result) {
        lastFront = front;
      }
    }
    return result;
  }

  public boolean isCoverage(List<S> front) {
    var coverage = this.indicator.compute(SolutionListUtils.getMatrixWithObjectiveValues(front));
    var result = coverage > coverageValue;
    if (result) {
      lastFront = front;
    } else if (lastFront != null) {
      coverage = this.indicator.compute(SolutionListUtils.getMatrixWithObjectiveValues(lastFront));
      result = coverage > coverageValue;
      if (result) {
        lastFront = front;
      }
    }
    return result;
  }

  public void updateFront(double[][] front) {
    try {
      this.indicator.setReferenceFront(front);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
