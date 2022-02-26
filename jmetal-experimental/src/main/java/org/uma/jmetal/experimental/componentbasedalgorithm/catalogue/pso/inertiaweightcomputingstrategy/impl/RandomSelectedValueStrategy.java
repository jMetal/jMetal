package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.inertiaweightcomputingstrategy.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.inertiaweightcomputingstrategy.InertiaWeightComputingStrategy;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class RandomSelectedValueStrategy implements InertiaWeightComputingStrategy {

  private double lowerBound;
  private double upperBound;

  public RandomSelectedValueStrategy() {
    this(0.5, 1.0);
  }

  public RandomSelectedValueStrategy(double lowerBound, double upperBound) {
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
