package org.uma.jmetal.algorithm.multiobjective.mosa.cooling.impl;

import org.uma.jmetal.algorithm.multiobjective.mosa.cooling.CoolingScheme;

/**
 * Linear cooling scheme used by simulated annealing algorithms
 */
public class Linear implements CoolingScheme {
  private final double beta ;

  public Linear(double beta) {
    if (beta <= 0) {
      throw new RuntimeException("The value of beta (" + beta + ") must be greater than zero") ;
    }
    this.beta = beta ;
  }

  @Override
  /**
   * The iteration number is not needed in this cooling scheme
   */
  public double updateTemperature(double temperature, int iteration) {
    return temperature - beta ;
  }

  public double getBeta() {
    return beta ;
  }
}
