package org.uma.jmetal.util.aggregativefunction.impl;

import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;

import java.util.stream.IntStream;

public class WeightedSum implements AggregativeFunction {

  @Override
  public double compute(double[] vector, double[] weightVector) {
      var sum = 0.0;
      for (var n = 0; n < vector.length; n++) {
          var v = weightVector[n] * vector[n];
          sum += v;
      }

      return sum;
  }

  @Override
  public void update(double[] vector) {
  }
}
