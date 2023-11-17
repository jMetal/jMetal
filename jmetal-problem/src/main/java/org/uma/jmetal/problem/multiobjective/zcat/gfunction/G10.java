package org.uma.jmetal.problem.multiobjective.zcat.gfunction;

import java.util.function.Function;

public class G10 implements Function<double[], double[]> {
  private final int numberOfVariables;
  private final int paretoSetDimension;

  public G10(int numberOfVariables, int paretoSetDimension) {
    this.numberOfVariables = numberOfVariables;
    this.paretoSetDimension = paretoSetDimension;
  }

  @Override
  public double[] apply(double[] y) {
    double[] g = new double[numberOfVariables - paretoSetDimension];

    for (int j = 1; j <= numberOfVariables - paretoSetDimension; ++j) {
      double sum = 0.0;
      for (int i = 1; i <= paretoSetDimension; ++i) {
        sum += Math.sin((4.0 * y[i - 1] - 2.0) * Math.PI + AngleCalculator.calculateTheta(j, paretoSetDimension, numberOfVariables));
      }
      g[j - 1] = Math.pow(sum, 3.0) / (2.0 * Math.pow(paretoSetDimension, 3.0)) + 0.5;

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









