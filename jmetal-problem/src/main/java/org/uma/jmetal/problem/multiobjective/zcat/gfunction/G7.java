package org.uma.jmetal.problem.multiobjective.zcat.gfunction;

import java.util.function.Function;

public class G7 implements Function<double[], double[]> {

  private final int numberOfVariables;
  private final int paretoSetDimension;

  public G7(int numberOfVariables, int paretoSetDimension) {
    this.numberOfVariables = numberOfVariables;
    this.paretoSetDimension = paretoSetDimension;
  }

  @Override
  public double[] apply(double[] y) {
    double[] g = new double[numberOfVariables - paretoSetDimension];

    for (int j = 1; j <= numberOfVariables - paretoSetDimension; ++j) {
      double mu = 0.0;
      for (int i = 1; i <= paretoSetDimension; ++i) {
        mu += y[i - 1];
      }
      mu /= paretoSetDimension;

      double numerator = mu + Math.exp(Math.sin(
          7.0 * Math.PI * mu - Math.PI / 2.0 + AngleCalculator.calculateTheta(j, paretoSetDimension,
              numberOfVariables))) - Math.exp(-1.0);
      double denominator = 1.0 + Math.exp(1) - Math.exp(-1.0);
      g[j - 1] = numerator / denominator;

      assertValidRange(g[j - 1]);
    }

    return g;
  }

  private static void assertValidRange(double value) {
    if (!(0 <= value && value <= 1.0)) {
      throw new IllegalArgumentException("Invalid value. It should be in the range [0, 1]");
    }
  }
}







