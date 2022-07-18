package org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.impl;

import org.uma.jmetal.util.errorchecking.Check;

public class LinearIncreasingStrategy implements InertiaWeightRangeBasedComputingStrategy {
  protected final double minimumWeight ;
  protected final double maximumWeight ;
  protected final int maximumNumberOfIterations;
  protected int computeCounter ;
  protected final int swamSize ;

  public LinearIncreasingStrategy(double minimumWeight, double maximumWeight, int maximumNumberOfIterations, int swamSize) {
    Check.that(maximumWeight >= minimumWeight, "The maximum weight " + maximumWeight +
        " is not higher or equal than minimum weight " + maximumWeight);
    Check.that(swamSize > 0, "The swarm size is zero");
    this.minimumWeight = minimumWeight ;
    this.maximumWeight = maximumWeight ;
    this.maximumNumberOfIterations = maximumNumberOfIterations;
    this.swamSize = swamSize ;
    this.computeCounter = 0 ;
  }

  @Override
  public double compute() {
    computeCounter++ ;
    var currentIteration = (computeCounter % swamSize) ;
    var weight = minimumWeight - (minimumWeight - maximumWeight) * currentIteration/maximumNumberOfIterations ;
    currentIteration ++ ;
    
    return weight ;
  }

  @Override
  public double getMinimumWeight() {
    return minimumWeight;
  }

  @Override
  public double getMaximumWeight() {
    return maximumWeight;
  }

  public int getMaximumNumberOfIterations() {
    return maximumNumberOfIterations;
  }

  public int getComputeCounter() {
    return computeCounter;
  }
}
