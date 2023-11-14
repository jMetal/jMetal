package org.uma.jmetal.problem.multiobjective.zcat.gfunction;

import java.util.function.Function;

public class G0 implements Function<double[], double[]> {
  int numberOfVariables ;
  int paretoSetDimension ;
  public G0(int numberOfVariables, int paretoSetDimension) {
    this.numberOfVariables = numberOfVariables ;
    this.paretoSetDimension = paretoSetDimension ;
  }

  @Override
  public double[] apply(double[] y) {
    double[] g = new double[numberOfVariables - paretoSetDimension] ;

    for (int j = 1; j <= numberOfVariables - paretoSetDimension; ++j) {
      g[j - 1] = 0.2210;
      assert(0 <= g[j - 1] && g[j - 1] <= 1.0);
    }
    return g;
  }
}
