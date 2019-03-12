package org.uma.jmetal.solution.integersolution.impl.util;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.util.VariableGenerator;

import java.util.List;

/**
 * Abstract class representing variable generator for double variables
 */
public abstract class IntegerVariableGenerator implements VariableGenerator<Integer> {
  protected List<Pair<Integer, Integer>> bounds ;
  protected boolean configured = false ;

  public void configure(List<Pair<Integer, Integer>> bounds) {
    this.bounds = bounds ;
    this.configured = true ;
  }
}
