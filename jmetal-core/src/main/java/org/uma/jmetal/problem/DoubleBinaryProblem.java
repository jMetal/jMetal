package org.uma.jmetal.problem;

import org.uma.jmetal.solution.Solution;

/** Interface representing problems having integer and double variables*/
public interface DoubleBinaryProblem<S extends Solution<Object>> extends Problem<S> {
  public Number getLowerBound(int index) ;
  public Number getUpperBound(int index) ;
  public int getNumberOfDoubleVariables() ;
  public int getNumberOfBits() ;
}
