package org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.impl;

import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.InertiaWeightComputingStrategy;

public interface InertiaWeightRangeBasedComputingStrategy extends InertiaWeightComputingStrategy {
  @Override
  double compute() ;

  double getMinimumWeight() ;
  double getMaximumWeight() ;
}
