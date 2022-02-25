package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.inertiaweightcomputingstrategy.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.inertiaweightcomputingstrategy.InertiaWeightComputingStrategy;

public class ConstantValueStrategy implements InertiaWeightComputingStrategy {
  private final double inertiaWeightValue ;

  public ConstantValueStrategy(double inertiaWeightValue) {
    this.inertiaWeightValue = inertiaWeightValue ;
  }

  @Override
  public double compute() {
    return inertiaWeightValue ;
  }
}
