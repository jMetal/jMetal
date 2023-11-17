package org.uma.jmetal.problem.multiobjective.zcat.ffunction;

import java.util.function.Function;

/**
 * F1: Shape of the PF (as in the paper)
 *
 * @param F: The shape of the PF
 * @param y: The first 'numberOfObjectives' normalized decision variables
 * @param numberOfObjectives: The number of objectives
 */
public class F12 implements Function<double[], double[]> {
  int numberOfObjectives ;

  public F12(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives ;
  }

  @Override
  public double[] apply(double[] y) {
    double[] F = new double[numberOfObjectives];
    double k = 3.0;

    F[0] = 1.0;
    for (int i = 1; i <= numberOfObjectives - 1; ++i) {
      F[0] *= (1.0 - y[i - 1]);
    }
    F[0] = 1.0 - F[0];
    assert (0 <= F[0] && F[0] <= 1.0);

    for (int j = 2; j <= numberOfObjectives - 1; ++j) {
      F[j - 1] = 1.0;
      for (int i = 1; i <= numberOfObjectives - j; ++i) {
        F[j - 1] *= (1.0 - y[i - 1]);
      }
      F[j - 1] *= y[numberOfObjectives - j + 1 - 1];
      F[j - 1] = 1.0 - F[j - 1];
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    F[numberOfObjectives - 1] = (Math.cos((2 * k - 1) * y[0] * Math.PI) + 2 * y[0] + 4 * k * (1 - y[0]) - 1) / (4 * k);
    assert (0 <= F[numberOfObjectives - 1] && F[numberOfObjectives - 1] <= 1.0);

    return F;
  }
}