package component.catalogue.pso.inertiaweightcomputingstrategy.impl;

import component.catalogue.pso.inertiaweightcomputingstrategy.InertiaWeightComputingStrategy;

public interface InertiaWeightRangeBasedComputingStrategy extends InertiaWeightComputingStrategy {
  @Override
  double compute() ;

  double getMinimumWeight() ;
  double getMaximumWeight() ;
}
