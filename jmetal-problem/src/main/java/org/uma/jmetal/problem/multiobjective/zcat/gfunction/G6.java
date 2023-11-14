package org.uma.jmetal.problem.multiobjective.zcat.gfunction;

import java.util.function.Function;

public class G6 implements Function<double[], double[]> {
  private final int numberOfVariables;
  private final int paretoSetDimension;

  public G6(int numberOfVariables, int paretoSetDimension) {
    this.numberOfVariables = numberOfVariables;
    this.paretoSetDimension = paretoSetDimension;
  }

  @Override
  public double[] apply(double[] y) {
    double[] g = new double[numberOfVariables - paretoSetDimension];

    for (int j = 1; j <= numberOfVariables - paretoSetDimension; ++j) {
      double s1 = 0.0;
      double s2 = 0.0;
      for (int i = 1; i <= paretoSetDimension; ++i) {
        s1 += Math.pow(y[i - 1], 2.0);
        s2 += Math.pow(Math.cos(11.0 * Math.PI * y[i - 1] + AngleCalculator.calculateTheta(j, paretoSetDimension, numberOfVariables)), 3.0);
      }
      s1 /= paretoSetDimension;
      s2 /= paretoSetDimension;

      double numerator = -10.0 * Math.exp((-2.0 / 5.0) * Math.sqrt(s1)) - Math.exp(s2) + 10.0 + Math.exp(1.0);
      double denominator = -10.0 * Math.exp(-2.0 / 5.0) - Math.pow(Math.exp(1.0), -1) + 10.0 + Math.exp(1.0);
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







