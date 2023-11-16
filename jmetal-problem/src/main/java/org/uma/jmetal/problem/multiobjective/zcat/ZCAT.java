package org.uma.jmetal.problem.multiobjective.zcat;

import static org.uma.jmetal.problem.multiobjective.zcat.util.ZCatZFunctions.Z1;
import static org.uma.jmetal.problem.multiobjective.zcat.util.ZCatZFunctions.Z2;
import static org.uma.jmetal.problem.multiobjective.zcat.util.ZCatZFunctions.Z3;
import static org.uma.jmetal.problem.multiobjective.zcat.util.ZCatZFunctions.Z4;
import static org.uma.jmetal.problem.multiobjective.zcat.util.ZCatZFunctions.Z5;
import static org.uma.jmetal.problem.multiobjective.zcat.util.ZCatZFunctions.Z6;
import static org.uma.jmetal.problem.multiobjective.zcat.util.ZCatZFunctions.Zbias;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;

public abstract class ZCAT extends AbstractDoubleProblem {
  public boolean complicatedParetoSet;
  public int level;
  public boolean bias;
  public boolean imbalance;

  protected ZCAT(int numberOfObjectives,
      int numberOfVariables,
      boolean complicatedParetoSet,
      int level,
      boolean bias, boolean imbalance) {
    numberOfObjectives(numberOfObjectives);
    this.complicatedParetoSet = complicatedParetoSet;
    this.level = level;
    this.bias = bias;
    this.imbalance = imbalance;

    setBounds(numberOfVariables);
  }

  private void setBounds(int numberOfVariables) {
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
   * Obtaining the values of B's functions
   */
  double[] zcatGetBeta(double[] normalizedVariables, int numberOfVariables,
      int paretoFrontDimension, boolean bias, boolean imbalance, int level,
      Function<double[], double[]> gFunction) {

    double[] z = zcatGetZ(normalizedVariables, paretoFrontDimension, gFunction) ;

    double[] w = zcatGetW(z, paretoFrontDimension, numberOfVariables, bias) ;
    double[] b = new double[numberOfObjectives] ;

    if (paretoFrontDimension == numberOfVariables) {
      for (int i = 1; i <= numberOfObjectives; ++i) {
        b[i-1] = 0.0 ;
      }
    } else {
      for (int i = 1; i <= numberOfObjectives; ++i) {
        double[] j = zcatGetJ(i, numberOfObjectives, w, numberOfVariables-paretoFrontDimension) ;
        double zValue = zcat_evaluate_Z(j, i, imbalance, level) ;
        b[i - 1] = (i * i) * zValue ;
      }
    }

    return b ;
  }

  /**
   * get the normalization of x
   *
   * @param x: Decision vector
   * @return: Normalization of x into the range [0,1]
   */
  public double[] zcatGetY(List<Double> x) {
    double[] y = new double[numberOfVariables()];

    for (int i = 0; i < numberOfVariables(); ++i) {
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

  /**
   * Modular approach.
   *
   * @param i:     Objective function
   * @param M:     Number of objectives
   * @param w:     The vector w=YII-g(YI)
   * @param wsize: The size of w (m-n)
   * @return The array J
   */
  public static double[] zcatGetJ(int i, int M, double[] w, int wsize) {
    double[] J = new double[0] ;
    int size = 0;

    for (int j = 1; j <= wsize; ++j) {
      if ((j - i) % M == 0) {
        size++;
        J = Arrays.copyOf(J, size);
        J[size - 1] = w[j - 1];
      }
    }

    if (size == 0) {
      size = 1;
      J = new double[size] ;
      J[size - 1] = w[0];
    }

    assert (size > 0);
    return J;
  }

  /**
   * Evaluate function Z according to the difficulty level
   *
   * @param w:              The 'w' vector given by the modular approach
   * @param ith_objective:  The index of the objective
   * @return The evaluated value of Z
   */
  public static double zcat_evaluate_Z(double[] w, int ith_objective, boolean imbalance, int level) {
    double z;

    if (imbalance) {
      if (ith_objective % 2 == 0) {
        z = Z4(w, w.length);
      } else {
        z = Z1(w, w.length);
      }
      return z;
    }

    switch (level) {
      /* Basis functions */
      case 1:
        z = Z1(w, w.length);
        break;
      case 2:
        z = Z2(w, w.length);
        break;
      case 3:
        z = Z3(w, w.length);
        break;
      case 4:
        z = Z4(w, w.length);
        break;
      case 5:
        z = Z5(w, w.length);
        break;
      case 6:
        z = Z6(w, w.length);
        break;
      default:
        z = Z1(w, w.length);
        break;
    }
    return z;
  }

  /**
   * Assigning objective values (additive approach)
   * @param alpha:    Alpha values
   * @param beta:     Beta values
   */
  double[] zcatMopDefinition(double[] alpha, double[] beta, int numberOfObjectives) {
    double[] f = new double[numberOfObjectives] ;
    int i;
    for (i = 1; i <= numberOfObjectives; ++i)
    {
      f[i - 1] = alpha[i - 1] + beta[i - 1]; /* Additive Approach */
    }
    return f ;
  }
}


