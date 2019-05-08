package org.uma.jmetal.solution;

import org.uma.jmetal.util.IndexBounder;

/**
 * Interface representing a solution having an array of real values and a bitset
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @deprecated Not used. For examples of multiple encodings, consider
 *             {@link IntegerDoubleSolution} instead.
 */
@Deprecated
public interface DoubleBinarySolution extends Solution<Object>, IndexBounder<Double>{
  public int getNumberOfDoubleVariables() ;
  public int getNumberOfBits() ;
}
