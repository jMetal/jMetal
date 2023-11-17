package org.uma.jmetal.problem.multiobjective.zcat.ffunction;

import static org.uma.jmetal.problem.multiobjective.zcat.util.ZCatUtils.zcatFixTo01;
import static org.uma.jmetal.problem.multiobjective.zcat.util.ZCatUtils.zcatValueIn;

import java.util.function.Function;

/**
 * F1: Shape of the PF (as in the paper)
 *
 * @param F: The shape of the PF
 * @param y: The first 'numberOfObjectives' normalized decision variables
 * @param numberOfObjectives: The number of objectives
 */
public class F19 implements Function<double[], double[]> {
  int numberOfObjectives ;

  public F19(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives ;
  }

  @Override
  public double[] apply(double[] y) {
    double[] F = new double[numberOfObjectives];
    int j;
    double A = 5.0;
    double mu;

    int flag_deg;

    flag_deg = (zcatValueIn(y[0], 0.0, 0.2) || zcatValueIn(y[0], 0.4, 0.6)) ? 1 : 0;
    mu = 0.0;
    for (j = 1; j <= numberOfObjectives - 1; ++j) {
      mu += y[j - 1];
      F[j - 1] = (flag_deg == 1) ? y[0] : y[j - 1];
      F[j - 1] = zcatFixTo01(F[j - 1]);
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }
    mu = (flag_deg == 1) ? y[0] : mu / (numberOfObjectives - 1);

    F[numberOfObjectives - 1] = (1.0 - mu - Math.cos(2.0 * A * Math.PI * mu + Math.PI / 2) / (2.0 * A * Math.PI));
    F[numberOfObjectives - 1] = zcatFixTo01(F[numberOfObjectives - 1]);
    assert (0 <= F[numberOfObjectives - 1] && F[numberOfObjectives - 1] <= 1.0);

    return F;
  }
}