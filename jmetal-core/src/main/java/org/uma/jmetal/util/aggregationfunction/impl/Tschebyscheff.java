package org.uma.jmetal.util.aggregationfunction.impl;

import org.uma.jmetal.util.aggregationfunction.AggregationFunction;
import org.uma.jmetal.util.point.impl.IdealPoint;

/**
 * Class that implements the Tschebyscheff aggregation function for multi-objective optimization problems.
 *
 * @author Antonio J. Nebro
 */

 public class Tschebyscheff implements AggregationFunction {

  private IdealPoint idealPoint;

  /**
   * Constructor for the Tschebyscheff class, initializing the idealPoint property to null.
   */
  public Tschebyscheff() {
    this.idealPoint = null;
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
  public double compute(double[] vector, double[] weightVector) {
    double maxFun = -1.0e+30;

    for (int n = 0; n < vector.length; n++) {
      double diff = Math.abs(vector[n] - idealPoint.value(n));

      double feval;
      if (weightVector[n] == 0) {
        feval = 0.000001 * diff;
      } else {
        feval = diff * weightVector[n];
      }
      if (feval > maxFun) {
        maxFun = feval;
      }
    }

    return maxFun;
  }

  /**
   * Updates the idealPoint property with the values in the input vector.
   * If idealPoint is null, a new IdealPoint instance is created with the length of the vector input.
   *
   * @param vector The input vector to be used for updating the idealPoint.
   */
  @Override
  public void update(double[] vector) {
    if (idealPoint == null) {
      idealPoint = new IdealPoint(vector.length);
    }
    idealPoint.update(vector);
  }
}
