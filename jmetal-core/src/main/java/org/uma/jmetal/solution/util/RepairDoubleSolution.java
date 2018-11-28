package org.uma.jmetal.solution.util;

import java.io.Serializable;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * Interface representing classes that check whether a value is between a lower and an upper bound.
 * If not, a value between those limits is returned
 */
public interface RepairDoubleSolution extends Serializable {
  /**
   * Checks if a given value is between its bounds and repairs it otherwise
   * @param value The value to be checked
   * @param lowerBound
   * @param upperBound
   * @return The same value if it is between the limits or a repaired value otherwise
   */
  public double repairSolutionVariableValue(double value, double lowerBound, double upperBound) ;
}
