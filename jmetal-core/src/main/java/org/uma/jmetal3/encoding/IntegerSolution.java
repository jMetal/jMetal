package org.uma.jmetal3.encoding;

import org.uma.jmetal3.core.Solution;

/**
 * Created by Antonio on 03/09/14.
 */
public interface IntegerSolution extends Solution<Integer> {
  public Integer getLowerBound(int index) ;
  public Integer getUpperBound(int index) ;
 }
