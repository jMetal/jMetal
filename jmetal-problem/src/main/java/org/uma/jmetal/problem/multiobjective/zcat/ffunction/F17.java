package org.uma.jmetal.problem.multiobjective.zcat.ffunction;

import static org.uma.jmetal.problem.multiobjective.zcat.util.ZCatUtils.zcatForAllValueIn;

import java.util.function.Function;

/**
 * F1: Shape of the PF (as in the paper)
 *
 * @param F:                  The shape of the PF
 * @param y:                  The first 'numberOfObjectives' normalized decision variables
 * @param numberOfObjectives: The number of objectives
 */
public class F17 implements Function<double[], double[]> {

  int numberOfObjectives;

  public F17(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives;
  }

  @Override
  public double[] apply(double[] y) {
    double[] F = new double[numberOfObjectives];
    boolean wedge_flag;
    double sum;

    wedge_flag = zcatForAllValueIn(y, numberOfObjectives - 1, 0.0, 0.5);

    sum = 0.0;
    for (int j = 1; j <= numberOfObjectives - 1; ++j) {
      if (wedge_flag) {
        F[j - 1] = y[0];
      } else {
        F[j - 1] = y[j - 1];
        sum += 1 - y[j - 1];
      }
      assert (0 <= F[j - 1] && F[j - 1] <= 1.0);
    }

    if (wedge_flag) {
      F[numberOfObjectives - 1] =
          (Math.pow(Math.exp(1.0 - y[0]), 8.0) - 1.0) / (Math.pow(Math.exp(1.0), 8.0) - 1.0);
    } else {
      F[numberOfObjectives - 1] =
          (Math.pow(Math.exp(sum / (numberOfObjectives - 1)), 8.0) - 1.0) / (
              Math.pow(Math.exp(1.0), 8.0) - 1.0);
    }
    assert (0 <= F[numberOfObjectives - 1] && F[numberOfObjectives - 1] <= 1.0);

    return F;
  }
}