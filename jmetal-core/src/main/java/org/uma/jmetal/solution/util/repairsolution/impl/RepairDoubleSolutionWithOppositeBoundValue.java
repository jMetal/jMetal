package org.uma.jmetal.solution.util.repairsolution.impl;

import org.uma.jmetal.solution.util.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.util.checking.Check;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
@SuppressWarnings("serial")
public class RepairDoubleSolutionWithOppositeBoundValue implements RepairDoubleSolution {
  /**
   * Checks if the value is between its bounds; if not, if it lower/higher than the lower/upper bound, the upper/lower
   * bound value is returned
   *
   * @param value The value to be checked
   * @param lowerBound
   * @param upperBound
   * @return The same value if it is in the limits or a repaired value otherwise
   */
  public double repairSolutionVariableValue(double value, double lowerBound, double upperBound) {
    Check.that(lowerBound < upperBound, "The lower bound (" + lowerBound + ") is greater than the "
            + "upper bound (" + upperBound+")");

    double result = value ;
    if (value < lowerBound) {
      result = upperBound ;
    }
    if (value > upperBound) {
      result = lowerBound ;
    }

    return result ;
  }
}
