package org.uma.jmetal.util.aggregationfunction.impl;

import org.uma.jmetal.util.aggregationfunction.AggregationFunction;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

public class WeightedSum implements AggregationFunction {
  private IdealPoint idealPoint ;
  private NadirPoint nadirPoint ;

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

  @Override
  public void reset() {
    idealPoint = null ;
    nadirPoint = null ;
  }
}
