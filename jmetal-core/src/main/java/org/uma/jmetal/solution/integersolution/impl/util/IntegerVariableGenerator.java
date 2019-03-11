package org.uma.jmetal.solution.integersolution.impl.util;

import org.uma.jmetal.solution.util.VariableGenerator;

import java.util.List;

/**
 * Abstract class representing variable generator for double variables
 */
public abstract class IntegerVariableGenerator implements VariableGenerator<Integer> {
  protected int numberOfVariables ;
  protected List<Integer> lowerBounds ;
  protected List<Integer> upperBounds ;
  protected boolean configured = false ;

  public void configure(int numberOfVariables, List<Integer> lowerBounds, List<Integer> upperBounds) {
    this.numberOfVariables = numberOfVariables ;
    this.lowerBounds = lowerBounds ;
    this.upperBounds = upperBounds ;
    this.configured = true ;
  }
}
