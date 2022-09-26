package org.uma.jmetal.util.aggregationfunction.impl;

import org.uma.jmetal.util.aggregationfunction.AggregationFunction;

public class WeightedSum implements AggregationFunction {

  @Override
  public double compute(double[] vector, double[] weightVector) {
    double sum = 0.0;
    for (int n = 0; n < vector.length; n++) {
      sum += weightVector[n] * vector[n];
    }

    return sum;
  }

  @Override
  public void update(double[] vector) {
  }
}
