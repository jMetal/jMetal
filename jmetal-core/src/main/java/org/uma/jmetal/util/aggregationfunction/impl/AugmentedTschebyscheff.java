package org.uma.jmetal.util.aggregationfunction.impl;

import org.uma.jmetal.util.aggregationfunction.AggregationFunction;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

/**
 * Augmented (a.k.a. modified) Tchebycheff scalarizing function. It extends the classic Tchebycheff
 * function with a small weighted-sum term controlled by {@code rho}. This augmentation removes the
 * weakly Pareto optimal solutions that the plain Tchebycheff function may produce, guaranteeing
 * that the minimizers of the function are properly Pareto optimal.
 *
 * <p>g(x | w, z*) = max_i ( w_i |f_i(x) - z*_i| ) + rho * sum_i ( w_i |f_i(x) - z*_i| )
 *
 * <p>Smaller values are better, so this function is used directly by decomposition-based
 * algorithms such as MOEA/D.
 *
 * <p>References:
 * <ul>
 *   <li>Steuer, R.E. and Choo, E.-U. (1983). An interactive weighted Tchebycheff procedure for
 *   multiple objective programming. Mathematical Programming, 26(3), 326-344.
 *   https://doi.org/10.1007/BF02591870</li>
 *   <li>Steuer, R.E. (1986). Multiple Criteria Optimization: Theory, Computation, and Application.
 *   John Wiley &amp; Sons, New York.</li>
 * </ul>
 */
public class AugmentedTschebyscheff implements AggregationFunction {
  private final double rho;
  private final boolean normalizeObjectives;
  private double epsilon = 0.000001;

  public AugmentedTschebyscheff(boolean normalizeObjectives) {
    this(0.0001, normalizeObjectives);
  }

  public AugmentedTschebyscheff(double rho, boolean normalizeObjectives) {
    this.rho = rho;
    this.normalizeObjectives = normalizeObjectives;
  }

  @Override
  public double compute(double[] vector, double[] weightVector, IdealPoint idealPoint,
      NadirPoint nadirPoint) {
    double maxFun = -1.0e+30;
    double sum = 0.0;

    for (int n = 0; n < vector.length; n++) {
      double diff;
      if (normalizeObjectives) {
        diff = Math.abs(
            (vector[n] - idealPoint.value(n)) / (nadirPoint.value(n) - idealPoint.value(n) + epsilon));
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
      sum += feval;
    }

    return maxFun + rho * sum;
  }

  @Override
  public void epsilon(double epsilon) {
    this.epsilon = epsilon;
  }

  @Override
  public boolean normalizeObjectives() {
    return this.normalizeObjectives;
  }
}
