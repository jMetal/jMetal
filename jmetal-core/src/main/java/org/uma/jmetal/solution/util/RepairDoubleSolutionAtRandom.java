package org.uma.jmetal.solution.util;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Created by ajnebro on 11/12/14.
 */
public class RepairDoubleSolutionAtRandom implements RepairDoubleSolution {
  private JMetalRandom randomGenerator ;

  /**
   * Constructor
   */
  public RepairDoubleSolutionAtRandom() {
    randomGenerator = JMetalRandom.getInstance() ;
  }
  /**
   * Checks if the value is between the bounds; if not, a random value between the limits is returned
   * @param value The value to be checked
   * @param lowerBound
   * @param upperBound
   * @return The same value if it is in the limits or a repaired value otherwise
   */
  public double repairSolutionVariableValue(double value, double lowerBound, double upperBound) {
    if (lowerBound > upperBound) {
      throw new JMetalException("The lower bound (" + lowerBound + ") is greater than the "
          + "upper bound (" + upperBound+")") ;
    }
    double result = value ;
    if (value < lowerBound) {
      result = randomGenerator.nextDouble(lowerBound, upperBound) ;
    }
    if (value > upperBound) {
      result = randomGenerator.nextDouble(lowerBound, upperBound) ;
    }

    return result ;
  }
}
