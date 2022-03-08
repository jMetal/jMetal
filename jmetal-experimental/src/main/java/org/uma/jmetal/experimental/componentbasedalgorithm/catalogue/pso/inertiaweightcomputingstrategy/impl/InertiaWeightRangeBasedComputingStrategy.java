package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.inertiaweightcomputingstrategy.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.inertiaweightcomputingstrategy.InertiaWeightComputingStrategy;

public interface InertiaWeightRangeBasedComputingStrategy extends InertiaWeightComputingStrategy {
  @Override
  double compute() ;

  double getMinimumWeight() ;
  double getMaximumWeight() ;
}
