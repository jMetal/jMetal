package org.uma.jmetal.problem.multiobjective.wfg;

/**
 * Class implementing shape functions for wfg benchmark
 * Reference: Simon Huband, Luigi Barone, Lyndon While, Phil Hingston
 * A Scalable Multi-objective Test Problem Toolkit.
 * Evolutionary Multi-Criterion Optimization:
 * Third International Conference, EMO 2005.
 * Proceedings, volume 3410 of Lecture Notes in Computer Science
 */
public class Shapes {

  /**
   * Calculate a linear shape
   */
  public float linear(float[] x, int m) {
    float result = (float) 1.0;
    int M = x.length;

    for (int i = 1; i <= M - m; i++) {
      result *= x[i - 1];
    }

    if (m != 1) {
      result *= (1 - x[M - m]);
    }

    return result;
  }

  /**
   * Calculate a convex shape
   */
  public float convex(float[] x, int m) {
    float result = (float) 1.0;
    int M = x.length;

    for (int i = 1; i <= M - m; i++) {
      result *= (1 - Math.cos(x[i - 1] * Math.PI * 0.5));
    }

    if (m != 1) {
      result *= (1 - Math.sin(x[M - m] * Math.PI * 0.5));
    }

    return result;
  }

  /**
   * Calculate a concave shape
   */
  public float concave(float[] x, int m) {
    float result = (float) 1.0;
    int M = x.length;

    for (int i = 1; i <= M - m; i++) {
      result *= Math.sin(x[i - 1] * Math.PI * 0.5);
    }

    if (m != 1) {
      result *= Math.cos(x[M - m] * Math.PI * 0.5);
    }

    return result;
  }

  /**
   * Calculate a mixed shape
   */
  public float mixed(float[] x, int A, float alpha) {
    float tmp;
    tmp =
      (float) Math.cos((float) 2.0 * A * (float) Math.PI * x[0] + (float) Math.PI * (float) 0.5);
    tmp /= (2.0 * (float) A * Math.PI);

    return (float) Math.pow(((float) 1.0 - x[0] - tmp), alpha);
  }

  /**
   * Calculate a disc shape
   */
  public float disc(float[] x, int A, float alpha, float beta) {
    float tmp;
    tmp = (float) Math.cos((float) A * Math.pow(x[0], beta) * Math.PI);

    return (float) 1.0 - (float) Math.pow(x[0], alpha) * (float) Math.pow(tmp, 2.0);
  }
}
