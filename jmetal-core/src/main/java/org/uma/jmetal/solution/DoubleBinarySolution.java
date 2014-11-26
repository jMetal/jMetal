package org.uma.jmetal.solution;

/**
 * Created by ajnebro on 24/11/14.
 * Interface representing a solution having an array of real values and a bitset
 */
public interface DoubleBinarySolution extends Solution<Object>{
  public int getNumberOfDoubleVariables() ;
  public Double getLowerBound(int index) ;
  public Double getUpperBound(int index) ;
  public int getNumberOfBits() ;
}
