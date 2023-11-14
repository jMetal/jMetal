package org.uma.jmetal.problem.multiobjective.zcat.gfunction;

import java.util.function.Function;

public class G2 implements Function<double[], double[]> {

  private final int numberOfVariables;
  private final int paretoSetDimension;

  public G2(int numberOfVariables, int paretoSetDimension) {
    this.numberOfVariables = numberOfVariables;
    this.paretoSetDimension = paretoSetDimension;
  }

  @Override
  public double[] apply(double[] y) {
    double[] g = new double[numberOfVariables - paretoSetDimension];

    for (int j = 1; j <= numberOfVariables - paretoSetDimension; ++j) {
      double sum = 0.0;
      for (int i = 1; i <= paretoSetDimension; ++i) {
        sum += Math.pow(y[i - 1], 2.0) * Math.sin(
            4.5 * Math.PI * y[i - 1] + AngleCalculator.calculateTheta(j, paretoSetDimension,
                numberOfVariables));
      }
      g[j - 1] = sum / (2.0 * paretoSetDimension) + 0.5;
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

