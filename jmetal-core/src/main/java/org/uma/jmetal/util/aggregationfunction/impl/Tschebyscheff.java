package org.uma.jmetal.util.aggregationfunction.impl;

import org.uma.jmetal.util.aggregationfunction.AggregationFunction;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

public class Tschebyscheff implements AggregationFunction {
  private boolean normalizeObjectives;
  private double epsilon = 0.000001 ;

  public Tschebyscheff(boolean normalizeObjectives) {
    this.normalizeObjectives = normalizeObjectives;
  }

  /**
   * Computes the Tschebyscheff aggregation function using the formula:
   * max(|vector[n] - idealPoint.value(n)| * weightVector[n]).
   * If weightVector[n] is equal to 0, feval is set to 0.000001 * diff.
   *
   * @param vector The input vector to be aggregated.
   * @param weightVector The weight vector to be used in the aggregation.
   * @return The result of the Tschebyscheff aggregation function.
   */
  @Override
  public double compute(double[] vector, double[] weightVector, IdealPoint idealPoint,
      NadirPoint nadirPoint) {
    double maxFun = -1.0e+30;

    for (int n = 0; n < vector.length; n++) {
      double diff;
      if (normalizeObjectives) {
        diff = Math.abs(
            (vector[n] - idealPoint.value(n)) / (nadirPoint.value(n) - idealPoint.value(n)+epsilon));
      } else {
        diff = Math.abs(vector[n] - idealPoint.value(n));
      }

      double feval;
      if (weightVector[n] == 0) {
        feval = 0.0001 * diff;
      } else {
        feval = diff * weightVector[n];
      }
      if (feval > maxFun) {
        maxFun = feval;
      }
    }

    return maxFun;
  }

  @Override
  public void epsilon(double epsilon) {
    this.epsilon = epsilon ;
  }

  @Override
  public boolean normalizeObjectives() {
    return this.normalizeObjectives;
  }
}