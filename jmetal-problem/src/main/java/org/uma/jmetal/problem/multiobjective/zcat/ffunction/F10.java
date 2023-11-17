package org.uma.jmetal.problem.multiobjective.zcat.ffunction;

import java.util.function.Function;

/**
 * F1: Shape of the PF (as in the paper)
 *
 * @param F: The shape of the PF
 * @param y: The first 'numberOfObjectives' normalized decision variables
 * @param numberOfObjectives: The number of objectives
 */
public class F10 implements Function<double[], double[]> {
  int numberOfObjectives ;

  public F10(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives ;
  }

  @Override
  public double[] apply(double[] y) {
    double[] F = new double[numberOfObjectives];

    double sum = 0.0;
    double r = 0.02;
    for (int j = 1; j <= numberOfObjectives - 1; ++j) {
      sum += 1 - y[j - 1];
      F[j - 1] = y[j - 1];
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    F[numberOfObjectives - 1] = (1 / r - 1 / (sum / (numberOfObjectives - 1) + r)) / (1 / r - 1 / (1 + r));
    assert (0 <= F[numberOfObjectives - 1] && F[numberOfObjectives - 1] <= 1.0);

    return F;
  }
}