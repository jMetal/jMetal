package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.inertiaweightcomputingstrategy.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.inertiaweightcomputingstrategy.InertiaWeightComputingStrategy;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class LinearDecreasingStrategy implements InertiaWeightComputingStrategy {
  private final double minimumWeight ;
  private final double maximumWeight ;
  private final int maximumNumberOfIterations;
  private int currentIteration ;

  public LinearDecreasingStrategy(double minimumWeight, double maximumWeight, int maximumNumberOfIterations) {
    this.minimumWeight = minimumWeight ;
    this.maximumWeight = maximumWeight ;
    this.maximumNumberOfIterations = maximumNumberOfIterations;
    this.currentIteration = 1;
  }

  @Override
  public double compute() {
    double weight = maximumWeight - (maximumWeight - minimumWeight) * currentIteration/maximumNumberOfIterations ;
    currentIteration ++ ;

    return weight ;
  }
}
