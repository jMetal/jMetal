package org.uma.jmetal.solution.util.repairsolution.impl;

import org.uma.jmetal.solution.util.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
@SuppressWarnings("serial")
public class RepairDoubleSolutionWithRandomValue implements RepairDoubleSolution {
  private BoundedRandomGenerator<Double> randomGenerator ;

  /**
   * Constructor
   */
  public RepairDoubleSolutionWithRandomValue() {
	  this((a, b) -> JMetalRandom.getInstance().nextDouble(a, b));
  }

  /**
   * Constructor
   */
  public RepairDoubleSolutionWithRandomValue(BoundedRandomGenerator<Double> randomGenerator) {
    this.randomGenerator = randomGenerator ;
  }
  /**
   * Checks if the value is between its bounds; if not, a random value between the limits is returned
   * @param value The value to be checked
   * @param lowerBound
   * @param upperBound
   * @return The same value if it is between the limits or a repaired value otherwise
   */
  public double repairSolutionVariableValue(double value, double lowerBound, double upperBound) {
    Check.that(lowerBound < upperBound, "The lower bound (" + lowerBound + ") is greater than the "
        + "upper bound (" + upperBound+")");

    double result = value ;
    if (value < lowerBound) {
      result = randomGenerator.getRandomValue(lowerBound, upperBound) ;
    }
    if (value > upperBound) {
      result = randomGenerator.getRandomValue(lowerBound, upperBound) ;
    }

    return result ;
  }
}
