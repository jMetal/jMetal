package org.uma.jmetal.problem.multiobjective.zcat.ffunction;

import java.util.function.Function;

/**
 * F1: Shape of the PF (as in the paper)
 *
 * @param F: The shape of the PF
 * @param y: The first 'numberOfObjectives' normalized decision variables
 * @param numberOfObjectives: The number of objectives
 */
public class F7 implements Function<double[], double[]> {
  int numberOfObjectives ;

  public F7(int numberOfObjectives) {
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
      sum += Math.pow(0.5 - y[i - 1], 5);
    }
    sum = sum / (2 * (numberOfObjectives - 1) * Math.pow(0.5, 5));

    F[numberOfObjectives - 1] = sum + 0.5;
    assert (0 <= F[numberOfObjectives - 1] && F[numberOfObjectives - 1] <= 1.0);

    return F;
  }
}