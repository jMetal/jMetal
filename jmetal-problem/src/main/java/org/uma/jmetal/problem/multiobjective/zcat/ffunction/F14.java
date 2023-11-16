package org.uma.jmetal.problem.multiobjective.zcat.ffunction;

import java.util.function.Function;

/**
 * F1: Shape of the PF (as in the paper)
 *
 * @param F: The shape of the PF
 * @param y: The first 'numberOfObjectives' normalized decision variables
 * @param numberOfObjectives: The number of objectives
 */
public class F14 implements Function<double[], double[]> {
  int numberOfObjectives ;

  public F14(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives ;
  }

  @Override
  public double[] apply(double[] y) {
    double[] F = new double[numberOfObjectives];

    F[0] = Math.pow(Math.sin(y[0] * Math.PI / 2), 2.0);
    assert (0 <= F[0] && F[0] <= 1.0);

    for (int j = 2; j <= numberOfObjectives - 2; ++j) {
      F[j - 1] = Math.pow(Math.sin(y[0] * Math.PI / 2), 2.0 + (j - 1.0) / (numberOfObjectives - 2.0));
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    if (numberOfObjectives > 2) {
      F[numberOfObjectives - 2] = 0.5 * (1 + Math.sin(6 * y[0] * Math.PI / 2 - Math.PI / 2));
      assert (0 <= F[numberOfObjectives - 2] && F[numberOfObjectives - 2] <= 1.0);
    }

    F[numberOfObjectives - 1] = Math.cos(y[0] * Math.PI / 2);
    assert (0 <= F[numberOfObjectives - 1] && F[numberOfObjectives - 1] <= 1.0);

    return F;
  }
}