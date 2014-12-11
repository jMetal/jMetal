package org.uma.jmetal.solution.util;

/**
 * Created by ajnebro on 11/12/14.
 */
public interface RepairDoubleSolution {
  /**
   * Checks if the value is between the bounds and repairs it otherwise
   * @param value The value to be checked
   * @param lowerBound
   * @param upperBound
   * @return The same value if it is in the limits or a repaired value otherwise
   */
  public double repairSolutionVariableValue(double value, double lowerBound, double upperBound) ;
}
