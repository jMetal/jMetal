package org.uma.jmetal.solution.doublesolution.impl.util;

import org.uma.jmetal.solution.util.VariableGenerator;

import java.util.List;

/**
 * Abstract class representing variable generator for double variables
 */
public abstract class DoubleVariableGenerator implements VariableGenerator<Double> {
  protected int numberOfVariables ;
  protected List<Double> lowerBounds ;
  protected List<Double> upperBounds ;
  protected boolean configured = false ;

  public void configure(int numberOfVariables, List<Double> lowerBounds, List<Double> upperBounds) {
    this.numberOfVariables = numberOfVariables ;
    this.lowerBounds = lowerBounds ;
    this.upperBounds = upperBounds ;
    this.configured = true ;
  }
}
