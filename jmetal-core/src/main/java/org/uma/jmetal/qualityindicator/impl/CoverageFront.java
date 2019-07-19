package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;

import java.util.List;

public class CoverageFront<S extends Solution<?>> {

  private double coverageValue;
  private double lastCoverageValue;
  private GenericIndicator<S> indicator;

  private List<S> lastFront;

  public CoverageFront(double coverageValue, GenericIndicator<S> indicator) {
    this.coverageValue = coverageValue;
    this.indicator = indicator;
    this.lastCoverageValue = 0;
  }

  public boolean isCoverageWithLast(List<S> front) {
    double coverage = this.indicator.evaluate(front);
    double aux = Math.abs(coverage - lastCoverageValue);
    lastCoverageValue = coverage;
    boolean result = aux > coverageValue;
    if (result) {
      lastFront = front;
    } else if (lastFront != null) {
      coverage = this.indicator.evaluate(lastFront);
      aux = Math.abs(coverage - lastCoverageValue);
      result = aux > coverageValue;
      if (result) {
        lastFront = front;
      }
    }
    return result;
  }

  public boolean isCoverage(List<S> front) {
    double coverage = this.indicator.evaluate(front);
    boolean result = coverage > coverageValue;
    if (result) {
      lastFront = front;
    } else if (lastFront != null) {
      coverage = this.indicator.evaluate(lastFront);
      result = coverage > coverageValue;
      if (result) {
        lastFront = front;
      }
    }
    return result;
  }

  public void updateFront(Front front) {
    try {
      this.indicator.setReferenceParetoFront(front);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
