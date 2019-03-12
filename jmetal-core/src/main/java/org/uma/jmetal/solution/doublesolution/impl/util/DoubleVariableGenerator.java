package org.uma.jmetal.solution.doublesolution.impl.util;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.util.VariableGenerator;

import java.util.List;

/**
 * Abstract class representing variable generator for double variables
 */
public abstract class DoubleVariableGenerator implements VariableGenerator<Double> {
  protected List<Pair<Double, Double>> bounds ;
  protected boolean configured = false ;

  public void configure(List<Pair<Double, Double>> bounds) {
    this.bounds = bounds ;
    this.configured = true ;
  }
}
