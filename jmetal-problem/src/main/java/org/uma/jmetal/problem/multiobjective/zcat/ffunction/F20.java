package org.uma.jmetal.problem.multiobjective.zcat.ffunction;

import static org.uma.jmetal.problem.multiobjective.zcat.util.ZCatUtils.zcatValueIn;

import java.util.function.Function;

/**
 * F1: Shape of the PF (as in the paper)
 *
 * @param F: The shape of the PF
 * @param y: The first 'numberOfObjectives' normalized decision variables
 * @param numberOfObjectives: The number of objectives
 */
public class F20 implements Function<double[], double[]> {
  int numberOfObjectives ;

  public F20(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives ;
  }

  @Override
  public double[] apply(double[] y) {
    double[] F = new double[numberOfObjectives];
    int j;
    int deg_flag;
    double sum;

    deg_flag = (zcatValueIn(y[0], 0.1, 0.4) || zcatValueIn(y[0], 0.6, 0.9)) ? 1 : 0;

    sum = 0.0;
    for (j = 1; j <= numberOfObjectives - 1; ++j) {
      sum += Math.pow(0.5 - y[j - 1], 5.0);
      F[j - 1] = (deg_flag == 1) ? y[0] : y[j - 1];
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    if (deg_flag == 1) {
      F[numberOfObjectives - 1] = (Math.pow(0.5 - y[0], 5.0) + Math.pow(0.5, 5.0)) / (2.0 * Math.pow(0.5, 5.0));
    } else {
      F[numberOfObjectives - 1] = sum / (2.0 * (numberOfObjectives - 1.0) * Math.pow(0.5, 5.0)) + 0.5;
    }
    assert (0 <= F[numberOfObjectives - 1] && F[numberOfObjectives - 1] <= 1.0);

    return F;
  }
}