package org.uma.jmetal.encoding;

import org.uma.jmetal.core.Solution;

/**
 * Created by Antonio on 03/09/14.
 */
public interface IntegerDoubleSolution extends Solution<Number> {
  public Number getLowerBound(int index) ;
  public Number getUpperBound(int index) ;
  public int getNumberOfIntegerVariables() ;
  public int getNumberOfDoubleVariables() ;
 }
