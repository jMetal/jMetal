package org.uma.jmetal.util.aggregativefunction.impl;

import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;

import java.util.stream.IntStream;

public class WeightedSum implements AggregativeFunction {

  @Override
  public double compute(double[] vector, double[] weightVector) {
    double sum = IntStream.range(0, vector.length).mapToDouble(n -> weightVector[n] * vector[n]).sum();

      return sum;
  }

  @Override
  public void update(double[] vector) {
  }
}
