package org.uma.jmetal.problem.multiobjective.wfg;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * This class implements the WFG3 problem
 * Reference: Simon Huband, Luigi Barone, Lyndon While, Phil Hingston
 * A Scalable Multi-objective Test Problem Toolkit.
 * Evolutionary Multi-Criterion Optimization:
 * Third International Conference, EMO 2005.
 * Proceedings, volume 3410 of Lecture Notes in Computer Science
 */
@SuppressWarnings("serial")
public class WFG3 extends WFG {

  /**
   * Creates a default WFG3 instances with
   * 2 position-related parameters
   * 4 distance-related parameters
   * and 2 objectives
   **/
  public WFG3() {
    this(2, 4, 2);
  }

  /**
   * Creates a WFG3 problem instance
   *
   * @param k            Number of position parameters
   * @param l            Number of distance parameters
   * @param m            Number of objective functions
   */
  public WFG3(Integer k, Integer l, Integer m) {
    super(k, l, m);
    setName("WFG3");

    s = new int[m];
    for (int i = 0; i < m; i++) {
      s[i] = 2 * (i + 1);
    }

    a = new int[m - 1];
    a[0] = 1;
    for (int i = 1; i < m - 1; i++) {
      a[i] = 0;
    }
  }

  public float[] evaluate(float[] z) {
    float[] y;

    y = normalise(z);
    y = t1(y, k);
    y = t2(y, k);
    y = t3(y, k, m);

    float[] result = new float[m];
    float[] x = calculateX(y);
    for (int m = 1; m <= this.m; m++) {
      result[m - 1] = d * x[this.m - 1] + s[m - 1] * (new Shapes()).linear(x, m);
    }

    return result;
  }

  /**
   * WFG3 t1 transformation
   */
  public float[] t1(float[] z, int k) {
    float[] result = new float[z.length];

    System.arraycopy(z, 0, result, 0, k);

    for (int i = k; i < z.length; i++) {
      result[i] = (new Transformations()).sLinear(z[i], (float) 0.35);
    }

    return result;
  }


  /**
   * WFG3 t2 transformation
   */
  public float[] t2(float[] z, int k) {
    float[] result = new float[z.length];

    System.arraycopy(z, 0, result, 0, k);

    int l = z.length - k;
    for (int i = k + 1; i <= k + l / 2; i++) {
      int head = k + 2 * (i - k) - 1;
      int tail = k + 2 * (i - k);
      float[] subZ = subVector(z, head - 1, tail - 1);

      result[i - 1] = (new Transformations()).rNonsep(subZ, 2);
    }
    return result;
  }

  /**
   * WFG3 t3 transformation
   */
  public float[] t3(float[] z, int k, int M) {
    float[] result = new float[M];
    float[] w = new float[z.length];

    for (int i = 0; i < z.length; i++) {
      w[i] = (float) 1.0;
    }

    for (int i = 1; i <= M - 1; i++) {
      int head = (i - 1) * k / (M - 1) + 1;
      int tail = i * k / (M - 1);
      float[] subZ = subVector(z, head - 1, tail - 1);
      float[] subW = subVector(w, head - 1, tail - 1);

      result[i - 1] = (new Transformations()).rSum(subZ, subW);
    }

    int l = z.length - k;
    int head = k + 1;
    int tail = k + l / 2;
    float[] subZ = subVector(z, head - 1, tail - 1);
    float[] subW = subVector(w, head - 1, tail - 1);
    result[M - 1] = (new Transformations()).rSum(subZ, subW);

    return result;
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to runAlgorithm
   * @throws JMetalException
   */
  public DoubleSolution evaluate(DoubleSolution solution) {
    float[] variables = new float[getNumberOfVariables()];
      double[] x = new double[10];
      int count = 0;
      int bound = getNumberOfVariables();
      for (int i1 = 0; i1 < bound; i1++) {
          double v = solution.variables().get(i1);
          if (x.length == count) x = Arrays.copyOf(x, count * 2);
          x[count++] = v;
      }
      x = Arrays.copyOfRange(x, 0, count);

      for (int i = 0; i < getNumberOfVariables(); i++) {
      variables[i] = (float) x[i] ;
    }

    float[] sol2 = evaluate(variables);

    for (int i = 0; i < sol2.length; i++) {
      solution.objectives()[i] = sol2[i];
    }
    return solution ;
  }
}
