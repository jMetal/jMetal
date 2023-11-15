package org.uma.jmetal.problem.multiobjective.zcat.ffunction;

import static org.uma.jmetal.problem.multiobjective.zcat.util.ZCatUtils.zcatFixTo01;

import java.util.function.Function;

/**
 * F1: Shape of the PF (as in the paper)
 *
 * @param F: The shape of the PF
 * @param y: The first 'm' normalized decision variables
 * @param M: The number of objectives
 */
public class F1 implements Function<double[], double[]> {
  int numberOfObjectives ;

  public F1(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives ;
  }

  @Override
  public double[] apply(double[] y) {
    double[] F = new double[numberOfObjectives] ;

    F[0] = 1.0;
    for (int i = 1; i <= numberOfObjectives - 1; i++) {
      F[0] *= Math.sin(y[i - 1] * Math.PI / 2.0);
    }
    F[0] = zcatFixTo01(F[0]);
    assert 0 <= F[0] && F[0] <= 1.0;

    for (int j = 2; j <= numberOfObjectives - 1; j++) {
      F[j - 1] = 1.0;
      for (int i = 1; i <= numberOfObjectives - j; i++) {
        F[j - 1] *= Math.sin(y[i - 1] * Math.PI / 2.0);
      }
      F[j - 1] *= Math.cos(y[numberOfObjectives - j + 1 - 1] * Math.PI / 2.0);
      F[j - 1] = zcatFixTo01(F[j - 1]);
      assert 0 <= F[j - 1] && F[j - 1] <= 1.0;
    }

    F[numberOfObjectives - 1] = 1.0 - Math.sin(y[0] * Math.PI / 2.0);
    F[numberOfObjectives - 1] = zcatFixTo01(F[numberOfObjectives - 1]);
    assert 0 <= F[numberOfObjectives - 1] && F[numberOfObjectives - 1] <= 1.0;
    return F;
  }
}