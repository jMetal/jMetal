package org.uma.jmetal.solution;

import org.uma.jmetal.util.IndexBounder;

/**
 * Interface representing a solution composed of integers and real values
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface IntegerDoubleSolution extends Solution<Number>, IndexBounder<Number> {
  public int getNumberOfIntegerVariables() ;
  public int getNumberOfDoubleVariables() ;
}
