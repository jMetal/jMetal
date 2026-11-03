package org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.impl;

import org.uma.jmetal.util.errorchecking.Check;

public class LinearDecreasingStrategy implements InertiaWeightRangeBasedComputingStrategy {
  protected final double minimumWeight ;
  protected final double maximumWeight ;
  protected final int maximumNumberOfIterations;
  protected int computeCounter ;
  protected final int swamSize ;

  public LinearDecreasingStrategy(double minimumWeight, double maximumWeight, int maximumNumberOfIterations, int swamSize) {
    Check.that(maximumWeight >= minimumWeight, "The maximum weight " + maximumWeight +
        " is not higher or equal than minimum weight " + minimumWeight);
    Check.that(swamSize > 0, "The swarm size is zero");
    Check.that(
        maximumNumberOfIterations > 0,
        () -> "The maximum number of iterations must be positive: " + maximumNumberOfIterations);
    this.minimumWeight = minimumWeight ;
    this.maximumWeight = maximumWeight ;
    this.maximumNumberOfIterations = maximumNumberOfIterations;
    this.swamSize = swamSize ;
    this.computeCounter = 0 ;
  }

  @Override
  public double compute() {
    // The weight is computed once per particle, so the iteration is the number of the call divided
    // by the size of the swarm; it does not go beyond the last iteration
    int currentIteration = Math.min(computeCounter / swamSize, maximumNumberOfIterations);
    computeCounter++ ;
    double weight = maximumWeight - (maximumWeight - minimumWeight) * currentIteration/maximumNumberOfIterations ;

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
