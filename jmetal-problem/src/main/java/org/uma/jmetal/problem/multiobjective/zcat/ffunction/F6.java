package org.uma.jmetal.problem.multiobjective.zcat.ffunction;

import java.util.function.Function;

/**
 * F1: Shape of the PF (as in the paper)
 *
 * @param F: The shape of the PF
 * @param y: The first 'numberOfObjectives' normalized decision variables
 * @param numberOfObjectives: The number of objectives
 */
public class F6 implements Function<double[], double[]> {
  int numberOfObjectives ;

  public F6(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives ;
  }

  @Override
  public double[] apply(double[] y) {
    double[] F = new double[numberOfObjectives];

    double k = 40.0;
    double r = 0.05;
    double mu = 0.0;

    for (int j = 1; j <= numberOfObjectives - 1; ++j) {
      F[j - 1] = y[j - 1];
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    for (int i = 1; i <= numberOfObjectives - 1; ++i) {
      mu += y[i - 1];
    }
    mu = mu / (numberOfObjectives - 1);

    F[numberOfObjectives - 1] = (Math.pow(1 + Math.exp(2 * k * mu - k), -1.0) - r * mu
        - Math.pow(1 + Math.exp(k), -1.0) + r)
        / (Math.pow(1 + Math.exp(-k), -1.0) - Math.pow(1 + Math.exp(k), -1.0) + r);
    assert (0 <= F[numberOfObjectives - 1] && F[numberOfObjectives - 1] <= 1.0);

    return F;
  }
}