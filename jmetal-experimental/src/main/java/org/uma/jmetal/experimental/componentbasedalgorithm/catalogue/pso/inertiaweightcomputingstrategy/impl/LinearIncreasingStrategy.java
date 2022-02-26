package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.inertiaweightcomputingstrategy.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.inertiaweightcomputingstrategy.InertiaWeightComputingStrategy;

public class LinearIncreasingStrategy implements InertiaWeightComputingStrategy {
  private final double minimumWeight ;
  private final double maximumWeight ;
  private final int maximumNumberOfIterations;
  private int currentIteration ;

  public LinearIncreasingStrategy(double minimumWeight, double maximumWeight, int maximumNumberOfIterations) {
    this.minimumWeight = minimumWeight ;
    this.maximumWeight = maximumWeight ;
    this.maximumNumberOfIterations = maximumNumberOfIterations;
    this.currentIteration = 1;
  }

  @Override
  public double compute() {
    double weight = minimumWeight - (minimumWeight - maximumWeight) * currentIteration/maximumNumberOfIterations ;
    currentIteration ++ ;
    
    return weight ;
  }

  public double getMinimumWeight() {
    return minimumWeight;
  }

  public double getMaximumWeight() {
    return maximumWeight;
  }

  public int getMaximumNumberOfIterations() {
    return maximumNumberOfIterations;
  }

  public int getCurrentIteration() {
    return currentIteration;
  }
}
