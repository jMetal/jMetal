package org.uma.jmetal.solution;

/**
 * Interface representing a solution having an array of real values and a bitset
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DoubleBinarySolution extends Solution<Object>{
  public int getNumberOfDoubleVariables() ;
  public Double getLowerBound(int index) ;
  public Double getUpperBound(int index) ;
  public int getNumberOfBits() ;
}
