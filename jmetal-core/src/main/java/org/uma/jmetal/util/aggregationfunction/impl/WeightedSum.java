package org.uma.jmetal.util.aggregationfunction.impl;

import org.uma.jmetal.util.aggregationfunction.AggregationFunction;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

public class WeightedSum implements AggregationFunction {

  private boolean normalizeObjectives;

  public WeightedSum(boolean normalizeObjectives) {
    this.normalizeObjectives = normalizeObjectives;
  }

  @Override
  public double compute(double[] vector, double[] weightVector, IdealPoint idealPoint,
      NadirPoint nadirPoint) {
    double sum = 0.0;
    for (int n = 0; n < vector.length; n++) {
      double value;
      if (normalizeObjectives) {
        value = (vector[n] - idealPoint.value(n)) / (nadirPoint.value(n) - idealPoint.value(n));
      } else {
        value = vector[n];
      }
      sum += weightVector[n] * value;
    }

    return sum;
  }

  @Override
  public boolean normalizeObjectives() {
    return this.normalizeObjectives;
  }
}