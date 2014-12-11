package org.uma.jmetal.solution.util;

/**
 * Created by ajnebro on 11/12/14.
 */
public class RepairDoubleSolutionAtBounds implements RepairDoubleSolution {
  /**
   * Checks if the value is between the bounds; if not, the lower or upper bound is returned
   * @param value The value to be checked
   * @param lowerBound
   * @param upperBound
   * @return The same value if it is in the limits or a repaired value otherwise
   */
  public double repairSolutionVariableValue(double value, double lowerBound, double upperBound) {
    double result = value ;
    if (value < lowerBound) {
      result = lowerBound ;
    }
    if (value > upperBound) {
      result = upperBound ;
    }

    return result ;
  }
}
