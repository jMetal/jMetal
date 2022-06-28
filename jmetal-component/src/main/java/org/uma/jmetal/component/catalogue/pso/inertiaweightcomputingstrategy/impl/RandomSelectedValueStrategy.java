package org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.impl;

import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.InertiaWeightComputingStrategy;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class RandomSelectedValueStrategy implements InertiaWeightComputingStrategy {

  private double lowerBound;
  private double upperBound;

  public RandomSelectedValueStrategy() {
    this(0.5, 1.0);
  }

  public RandomSelectedValueStrategy(double lowerBound, double upperBound) {
    Check.that(upperBound >= lowerBound, "The upper bound " + upperBound +
        " is not higher or equal than lower bound" + lowerBound);
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  @Override
  public double compute() {
    return JMetalRandom.getInstance().nextDouble(lowerBound, upperBound);
  }

  public double getLowerBound() {
    return lowerBound;
  }

  public double getUpperBound() {
    return upperBound;
  }
}
