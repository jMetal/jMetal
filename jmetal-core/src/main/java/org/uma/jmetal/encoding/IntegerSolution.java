package org.uma.jmetal.encoding;

import org.uma.jmetal.core.Solution;

/**
 * Created by Antonio on 03/09/14.
 */
public interface IntegerSolution extends Solution<Integer> {
  public Integer getLowerBound(int index) ;
  public Integer getUpperBound(int index) ;
 }
