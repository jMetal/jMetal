package org.uma.jmetal.problem.multiobjective.zcat.util;

import static java.lang.Math.*;

public class ZCatZFunctions {

  private static final double DBL_MAX = Double.MAX_VALUE;

  /**
   * Z1 function
   * @param J:     Subset of decision variables to consider from YII - g(YI) (cf the paper)
   * @param Jsize: The size of J
   * @return
   */
  public static double Z1(double[] J, int Jsize) {
    assert Jsize > 0;
    double Z = 0.0;
    for (int i = 0; i < Jsize; i++) {
      Z += J[i] * J[i];
    }
    Z = (10.0 / Jsize) * Z;
    assert 0 <= Z && Z <= 10.0;
    return Z;
  }

  /**
   * Z2 function
   * @param J:     Subset of decision variables to consider from YII - g(YI) (cf the paper)
   * @param Jsize: The size of J
   * @return
   */
  public static double Z2(double[] J, int Jsize) {
    assert Jsize > 0;
    double Z = -DBL_MAX;
    for (int i = 0; i < Jsize; i++) {
      Z = max(Z, abs(J[i]));
    }
    Z = 10.0 * Z;
    assert 0 <= Z && Z <= 10.0;
    return Z;
  }

  /**
   * Z3 function
   * @param J:     Subset of decision variables to consider from YII - g(YI) (cf the paper)
   * @param Jsize: The size of J
   * @return
   */
  public static double Z3(double[] J, int Jsize) {
    assert Jsize > 0;
    double Z = 0.0;
    double k = 5.0;
    for (int i = 0; i < Jsize; i++) {
      Z += (pow(J[i], 2.0) - cos((2.0 * k - 1) * PI * J[i]) + 1.0) / 3.0;
    }
    Z = (10.0 / Jsize) * Z;
    assert 0 <= Z && Z <= 10.0;
    return Z;
  }

  /**
   * Z4 function
   * @param J:     Subset of decision variables to consider from YII - g(YI) (cf the paper)
   * @param Jsize: The size of J
   * @return
   */
  public static double Z4(double[] J, int Jsize) {
    assert Jsize > 0;
    double Z = 0.0;
    double k = 5.0;
    double pow1 = -DBL_MAX;
    double pow2 = 0.0;
    for (int i = 0; i < Jsize; i++) {
      pow1 = max(pow1, abs(J[i]));
      pow2 += 0.5 * (cos((2.0 * k - 1) * PI * J[i]) + 1.0);
    }
    Z = (10.0 / (2.0 * exp(1) - 2.0)) * (exp(pow(pow1, 0.5)) - exp(pow2 / Jsize) - 1.0 + exp(1));
    assert 0 <= Z && Z <= 10.0;
    return Z;
  }

  /**
   * Z5 function
   * @param J:     Subset of decision variables to consider from YII - g(YI) (cf the paper)
   * @param Jsize: The size of J
   * @return
   */
  public static double Z5(double[] J, int Jsize) {
    assert Jsize > 0;
    double Z = 0.0;
    for (int i = 0; i < Jsize; i++) {
      Z += pow(abs(J[i]), 0.002);
    }
    Z = -0.7 * Z3(J, Jsize) + (10.0 / Jsize) * Z;
    assert 0 <= Z && Z <= 10.0;
    return Z;
  }

  /**
   * Z6 function
   * @param J:     Subset of decision variables to consider from YII - g(YI) (cf the paper)
   * @param Jsize: The size of J
   * @return
   */
  public static double Z6(double[] J, int Jsize) {
    assert Jsize > 0;
    double Z = 0.0;
    for (int i = 0; i < Jsize; i++) {
      Z += abs(J[i]);
    }
    Z = -0.7 * Z4(J, Jsize) + 10.0 * pow(Z / Jsize, 0.002);
    assert 0 <= Z && Z <= 10.0;
    return Z;
  }

  /**
   * Bias transformation
   * @param z: The value to bias
   * @return
   */
  public static double Zbias(double z) {
    double w;
    double gamma = 0.05;
    w = pow(abs(z), gamma);
    assert 0.0 <= w && w <= 1.0;
    return w;
  }
}
