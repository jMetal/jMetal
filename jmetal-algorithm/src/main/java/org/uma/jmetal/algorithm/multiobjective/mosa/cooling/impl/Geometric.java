package org.uma.jmetal.algorithm.multiobjective.mosa.cooling.impl;

import org.uma.jmetal.algorithm.multiobjective.mosa.cooling.CoolingScheme;

/**
 * Geometric cooling scheme used by {@link SimulatedAnnealing} algorithms
 */
public class Geometric implements CoolingScheme {
  private final double alpha ;

  public Geometric(double alpha) {
    if ((alpha < 0.0) || (alpha > 1.0)) {
      throw new RuntimeException("The value of alpha (" + alpha + ") must be a number between 0 and 1") ;
    }
    this.alpha = alpha ;
  }

  @Override
  /**
   * The iteration number is not needed in this cooling scheme
   */
  public double updateTemperature(double temperature, int iteration) {
    return alpha * temperature ;
  }

  public double getAlpha() {
    return alpha ;
  }
}
