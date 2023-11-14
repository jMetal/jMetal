package org.uma.jmetal.problem.multiobjective.zcat;

import static org.uma.jmetal.problem.multiobjective.zcat.util.ZCatZFunctions.Zbias;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;

public abstract class ZCAT extends AbstractDoubleProblem {

  public int numberOfObjectives;
  public int numberOfVariables;
  public boolean complicatedParetoSet;
  public int level;
  public boolean bias;
  public boolean imbalance;

  public ZCAT(int numberOfObjectives,
      int numberOfVariables,
      boolean complicatedParetoSet,
      int level,
      boolean bias, boolean imbalance) {
    this.numberOfObjectives = numberOfObjectives;
    this.numberOfVariables = numberOfVariables;
    this.complicatedParetoSet = complicatedParetoSet;
    this.level = level;
    this.bias = bias;
    this.imbalance = imbalance;

    setBounds();
  }

  private void setBounds() {
    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Double> upperLimit = new ArrayList<>(numberOfVariables);

    for (int i = 1; i < numberOfVariables + 1; ++i) {
      lowerLimit.add(-i * 0.5);
      upperLimit.add(i * 0.5);
    }

    variableBounds(lowerLimit, upperLimit);
  }


  /**
   * Obtaining the values of alpha (eq. 5 in the paper)
   *
   * @param y
   * @param M
   * @param f_function
   * @return
   */
  double[] zcatGetAlpha(double[] normalizedVariables, int numberOfObjectives,
      Function<double[], double[]> fFunction) {
    double[] a = fFunction.apply(normalizedVariables);

    for (int i = 1; i <= numberOfObjectives; ++i) {
      a[i - 1] = (i * i) * a[i - 1];
    }
    return a;
  }

  /**
   * get the normalization of x
   *
   * @param x: Decision vector
   * @return: Normalization of x into the range [0,1]
   */
  public double[] zcatGetY(List<Double> x) {
    double[] y = new double[numberOfVariables];

    for (int i = 0; i < numberOfVariables; ++i) {
      double lowerBound = variableBounds().get(i).getLowerBound();
      double upperBound = variableBounds().get(i).getUpperBound();
      y[i] = (x.get(i) - lowerBound) / (upperBound - lowerBound);
      assert (0.0 <= y[i] && y[i] <= 1.0);
    }
    return (y);
  }

  /**
   * Define z_{1:n-m} = (y_{m+1}-g_{m+1}, ..., y_n-g_n)
   *
   * @param y:                    Decision variables
   * @param paretoFrontDimension: Dimension of PF
   * @param gFunction:            g function (g0, g1, ..., g10)
   * @return z:         Array of z values
   */
  public double[] zcatGetZ(double[] y, int paretoFrontDimension,
      Function<double[], double[]> gFunction) {

    double[] g = gFunction.apply(y);
    double[] z = new double[y.length - paretoFrontDimension];
    for (int i = 0, j = paretoFrontDimension + 1; j <= y.length; ++j, ++i) {
      double diff = y[j - 1] - g[i];
      z[i] = (Math.abs(diff) < Double.MIN_VALUE) ? 0.0 : diff; /* precision issues */
    }

    return z;
  }

  /**
   * Define w_{1:n-m} = (Zbias(z_{1}),..., Zbias(z_{n-m})) if bias flag is activated
   *
   * @param z: z_{1:n-m} = (y_{m+1}-g_{m+1}, ..., y_n-g_n)
   * @param m: Dimension of PF
   * @param n: Number of decision variables
   * @return w: Array of w values
   */
  public static double[] zcatGetW(double[] z, int m, int n, boolean bias) {
    int i;
    double[] w = new double[n - m];

    for (i = 0; i < n - m; ++i) {
      w[i] = (bias) ? Zbias(z[i]) : z[i];
    }

    return w;
  }
}


