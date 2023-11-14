package org.uma.jmetal.problem.multiobjective.zcat.gfunction;

import java.util.function.Function;

public class G1 implements Function<double[], double[]> {

  private final int numberOfVariables;
  private final int paretoSetDimension;

  public G1(int numberOfVariables, int paretoSetDimension) {
    this.numberOfVariables = numberOfVariables;
    this.paretoSetDimension = paretoSetDimension;
  }

  @Override
  public double[] apply(double[] y) {
    double[] g = new double[numberOfVariables - paretoSetDimension];

    for (int j = 1; j <= numberOfVariables - paretoSetDimension; ++j) {
      double sum = 0.0;
      for (int i = 1; i <= paretoSetDimension; ++i) {
        sum += Math.sin(
            1.5 * Math.PI * y[i - 1] + AngleCalculator.calculateTheta(j, paretoSetDimension,
                numberOfVariables));
      }
      g[j - 1] = sum / (2.0 * paretoSetDimension) + 0.5;
    }

    return g;
  }
}
