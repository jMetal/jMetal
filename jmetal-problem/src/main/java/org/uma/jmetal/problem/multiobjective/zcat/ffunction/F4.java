package org.uma.jmetal.problem.multiobjective.zcat.ffunction;

import java.util.function.Function;

/**
 * F1: Shape of the PF (as in the paper)
 *
 * @param F: The shape of the PF
 * @param y: The first 'm' normalized decision variables
 * @param M: The number of objectives
 */
public class F4 implements Function<double[], double[]> {
  int numberOfObjectives ;

  public F4(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives ;
  }

  @Override
  public double[] apply(double[] y) {
    double[] F = new double[numberOfObjectives];

    for (int j = 1; j <= numberOfObjectives - 1; ++j) {
      F[j - 1] = y[j - 1];
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    double sum = 0.0;
    for (int i = 1; i <= numberOfObjectives - 1; ++i) {
      sum += y[i - 1];
    }

    F[numberOfObjectives - 1] = 1.0 - sum / (numberOfObjectives - 1);
    assert (0 <= F[numberOfObjectives - 1] && F[numberOfObjectives - 1] <= 1.0);

    return F;
  }
}