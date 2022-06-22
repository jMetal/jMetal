package org.uma.jmetal.auto.component.catalogue.pso.inertiaweightcomputingstrategy.impl;

import org.uma.jmetal.auto.component.catalogue.pso.inertiaweightcomputingstrategy.InertiaWeightComputingStrategy;

public class ConstantValueStrategy implements InertiaWeightComputingStrategy {
  private final double inertiaWeightValue ;

  public ConstantValueStrategy(double inertiaWeightValue) {
    this.inertiaWeightValue = inertiaWeightValue ;
  }

  @Override
  public double compute() {
    return inertiaWeightValue ;
  }

  public double getInertiaWeightValue() {
    return inertiaWeightValue;
  }
}
