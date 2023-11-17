package org.uma.jmetal.problem.multiobjective.zcat.gfunction;

import java.util.function.Function;

public class G4 implements Function<double[], double[]> {
  private final int numberOfVariables;
  private final int paretoSetDimension;

  public G4(int numberOfVariables, int paretoSetDimension) {
    this.numberOfVariables = numberOfVariables;
    this.paretoSetDimension = paretoSetDimension;
  }

  @Override
  public double[] apply(double[] y) {
    double[] g = new double[numberOfVariables - paretoSetDimension];

    for (int j = 1; j <= numberOfVariables - paretoSetDimension; ++j) {
      double mu = 0.0;
      for (int i = 1; i <= paretoSetDimension; ++i) {
        mu += y[i - 1] ;
      }
      mu = mu / paretoSetDimension;
      g[j - 1] = (mu / 2.0) * Math.cos(4.0 * Math.PI * mu + AngleCalculator.calculateTheta(j, paretoSetDimension, numberOfVariables)) + 0.5;
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






