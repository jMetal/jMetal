package org.uma.jmetal.problem.multiobjective.wfg;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class implementing the basics transformations for wfg
 */
public class Transformations {

  /**
   * Stores a default epsilon value
   */
  private static final float EPSILON = (float) 1.0e-10;

  /**
   * bPoly transformation
   *
   * @throws JMetalException
   */
  public float bPoly(float y, float alpha) throws JMetalException {
    if (!(alpha > 0)) {

      JMetalLogger.logger.severe("wfg.Transformations.bPoly: Param alpha " +
        "must be > 0");
      Class<String> cls = String.class;
      @NotNull String name = cls.getName();
      throw new JMetalException("Exception in " + name + ".bPoly()");
    }

    return correctTo01((float) StrictMath.pow(y, alpha));
  }

  /**
   * bFlat transformation
   */
  public float bFlat(float y, float A, float B, float C) {
    float tmp1 = Math.min((float) 0, (float) Math.floor(y - B)) * A * (B - y) / B;
    float tmp2 = Math.min((float) 0, (float) Math.floor(C - y)) * (1 - A) * (y - C) / (1 - C);

    return correctTo01(A + tmp1 - tmp2);
  }

  /**
   * sLinear transformation
   */
  public float sLinear(float y, float A) {
    return correctTo01(Math.abs(y - A) / (float) Math.abs(Math.floor(A - y) + A));
  }

  /**
   * sDecept transformation
   */
  public float sDecept(float y, float A, float B, float C) {
    float tmp, tmp1, tmp2;

    tmp1 = (float) Math.floor(y - A + B) * ((float) 1.0 - C + (A - B) / B) / (A - B);
    tmp2 =
      (float) Math.floor(A + B - y) * ((float) 1.0 - C + ((float) 1.0 - A - B) / B) / ((float) 1.0
        - A - B);

    tmp = Math.abs(y - A) - B;

    return correctTo01((float) 1 + tmp * (tmp1 + tmp2 + (float) 1.0 / B));
  }

  /**
   * sMulti transformation
   */
  public float sMulti(float y, int A, int B, float C) {
    float tmp1, tmp2;

    tmp1 = ((float) 4.0 * A + (float) 2.0) *
      (float) Math.PI *
      ((float) 0.5 - Math.abs(y - C) / ((float) 2.0 * ((float) Math.floor(C - y) + C)));
    tmp2 = (float) 4.0 * B *
      (float) StrictMath.pow(Math.abs(y - C) / ((float) 2.0 * ((float) Math.floor(C - y) + C))
        , (float) 2.0);

    return correctTo01(((float) 1.0 + (float) Math.cos(tmp1) + tmp2) / (B + (float) 2.0));
  }

  /**
   * rSum transformation
   */
  public float rSum(float @NotNull [] y, float[] w) {
    float tmp1 = (float) 0.0, tmp2 = (float) 0.0;
    for (int i = 0; i < y.length; i++) {
      tmp1 += y[i] * w[i];
      tmp2 += w[i];
    }

    return correctTo01(tmp1 / tmp2);
  }

  /**
   * rNonsep transformation
   */
  public float rNonsep(float @NotNull [] y, int A) {
    float tmp, denominator, numerator;

    tmp = (float) Math.ceil(A / (float) 2.0);
    denominator = y.length * tmp * ((float) 1.0 + (float) 2.0 * A - (float) 2.0 * tmp) / A;
    numerator = (float) 0.0;
    for (int j = 0; j < y.length; j++) {
      numerator += y[j];
      for (int k = 0; k <= A - 2; k++) {
        numerator += Math.abs(y[j] - y[(j + k + 1) % y.length]);
      }
    }

    return correctTo01(numerator / denominator);
  }

  /**
   * bParam transformation
   */
  public float bParam(float y, float u, float A, float B, float C) {
    float result, v, exp;

    v = A - ((float) 1.0 - (float) 2.0 * u) *
      Math.abs((float) Math.floor((float) 0.5 - u) + A);
    exp = B + (C - B) * v;
    result = (float) StrictMath.pow(y, exp);

    return correctTo01(result);
  }

  /**
   */
  float correctTo01(float a) {
    float min = (float) 0.0;
    float max = (float) 1.0;
    float min_epsilon = min - EPSILON;
    float max_epsilon = max + EPSILON;

    if ((a <= min && a >= min_epsilon) || (a >= min && a <= min_epsilon)) {
      return min;
    } else if ((a >= max && a <= max_epsilon) || (a <= max && a >= max_epsilon)) {
      return max;
    } else {
      return a;
    }
  }
}
