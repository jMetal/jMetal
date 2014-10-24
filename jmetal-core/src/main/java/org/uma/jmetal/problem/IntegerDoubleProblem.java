package org.uma.jmetal.problem;

import org.uma.jmetal.solution.Solution;

/** Interface representing problems having integer and double variables*/
public interface IntegerDoubleProblem<S extends Solution<Number>> extends Problem<S> {
  public Number getLowerBound(int index) ;
  public Number getUpperBound(int index) ;
  public int getNumberOfIntegerVariables() ;
  public int getNumberOfDoubleVariables() ;
}
