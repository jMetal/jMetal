package org.uma.jmetal.problem.multiobjective.wfg;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Creates a default WFG8 problem with
 * 2 position-related parameters,
 * 4 distance-related parameters,
 * and 2 objectives
 */
@SuppressWarnings("serial")
public class WFG8 extends WFG {

  /**
   * Creates a default WFG8 with
   * 2 position-related parameters,
   * 4 distance-related parameters,
   * and 2 objectives
   **/
  public WFG8() {
    this(2, 4, 2);
  }

  /**
   * Creates a WFG8 problem instance
   *
   * @param k            Number of position parameters
   * @param l            Number of distance parameters
   * @param m            Number of objective functions
   */
  public WFG8(Integer k, Integer l, Integer m) {
    super(k, l, m);
    setName("WFG8");

    s = new int[m];
    for (int i = 0; i < m; i++) {
      s[i] = 2 * (i + 1);
    }

    a = new int[m - 1];
    for (int i = 0; i < m - 1; i++) {
      a[i] = 1;
    }
  }

  /** Evaluate() method */
  public float[] evaluate(float[] z) {
    float[] y;

    y = normalise(z);
    y = t1(y, k);
    y = t2(y, k);
    y = t3(y, k, m);

    float[] result = new float[m];
    float[] x = calculateX(y);
    for (int m = 1; m <= this.m; m++) {
      result[m - 1] = d * x[this.m - 1] + s[m - 1] * (new Shapes()).concave(x, m);
    }

    return result;
  }

  /**
   * WFG8 t1 transformation
   */
  public float[] t1(float[] z, int k) {
    float @NotNull [] result = new float[z.length];
    float[] w = new float[z.length];

    for (int i = 0; i < w.length; i++) {
      w[i] = (float) 1.0;
    }

    System.arraycopy(z, 0, result, 0, k);

    for (int i = k; i < z.length; i++) {
      int head = 0;
      int tail = i - 1;
      float[] subZ = subVector(z, head, tail);
      float[] subW = subVector(w, head, tail);
      float aux = (new Transformations()).rSum(subZ, subW);

      result[i] =
        (new Transformations()).bParam(z[i], aux, (float) 0.98 / (float) 49.98, (float) 0.02, 50);
    }

    return result;
  }

  /**
   * WFG8 t2 transformation
   */
  public float[] t2(float[] z, int k) {
    float[] result = new float[z.length];

    System.arraycopy(z, 0, result, 0, k);

    for (int i = k; i < z.length; i++) {
      result[i] = (new Transformations()).sLinear(z[i], (float) 0.35);
    }

    return result;
  }

  /**
   * WFG8 t3 transformation
   */
  public float[] t3(float @NotNull [] z, int k, int M) {
    float @NotNull [] result = new float[M];
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

    int head = k + 1;
    int tail = z.length;
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
    float @NotNull [] variables = new float[getNumberOfVariables()];
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


