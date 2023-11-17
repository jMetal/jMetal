package org.uma.jmetal.problem.multiobjective.zcat.ffunction;

import java.util.function.Function;

/**
 * F1: Shape of the PF (as in the paper)
 *
 * @param F: The shape of the PF
 * @param y: The first 'numberOfObjectives' normalized decision variables
 * @param numberOfObjectives: The number of objectives
 */
public class F15 implements Function<double[], double[]> {
  int numberOfObjectives ;

  public F15(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives ;
  }

  @Override
  public double[] apply(double[] y) {
    double[] F = new double[numberOfObjectives];
    double k = 3.0;

    for (int j = 1; j <= numberOfObjectives - 1; ++j) {
      F[j - 1] = Math.pow(y[0], 1.0 + (j - 1.0) / (4.0 * numberOfObjectives));
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    F[numberOfObjectives - 1] = (Math.cos((2 * k - 1) * y[0] * Math.PI) + 2 * y[0] + 4 * k * (1 - y[0]) - 1) / (4 * k);
    assert (0 <= F[numberOfObjectives - 1] && F[numberOfObjectives - 1] <= 1.0);

    return F;
  }
}