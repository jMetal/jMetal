/**
 * WFG9.java
 * @author Juan J. Durillo
 * @version 1.0
 */
package org.uma.jmetal.problem.multiobjective.wfg;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

/**
 * Creates a default WFG9 problem with
 * 2 position-related parameters,
 * 4 distance-related parameters,
 * and 2 objectives
 */
public class WFG9 extends WFG {
  /**
   * Creates a default WFG9 with
   * 2 position-related parameters,
   * 4 distance-related parameters,
   * and 2 objectives
   */
  public WFG9() throws ClassNotFoundException, JMetalException {
    this(2, 4, 2);
  }

  /**
   * Creates a WFG9 problem instance
   *
   * @param k            Number of position variables
   * @param l            Number of distance variables
   * @param m            Number of objective functions
   */
  public WFG9(Integer k, Integer l, Integer m)
    throws ClassNotFoundException, JMetalException {
    super(k, l, m);
    setName("WFG9");

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
   * WFG9 t1 transformation
   */
  public float[] t1(float[] z, int k) {
    float[] result = new float[z.length];
    float[] w = new float[z.length];

    for (int i = 0; i < w.length; i++) {
      w[i] = (float) 1.0;
    }

    for (int i = 0; i < z.length - 1; i++) {
      int head = i + 1;
      int tail = z.length - 1;
      float[] subZ = subVector(z, head, tail);
      float[] subW = subVector(w, head, tail);
      float aux = (new Transformations()).rSum(subZ, subW);
      result[i] = (new Transformations())
        .bParam(z[i], aux, (float) 0.98 / (float) 49.98, (float) 0.02, (float) 50);
    }

    result[z.length - 1] = z[z.length - 1];
    return result;
  }

  /**
   * WFG9 t2 transformation
   */
  public float[] t2(float[] z, int k) {
    float[] result = new float[z.length];

    for (int i = 0; i < k; i++) {
      result[i] = (new Transformations()).sDecept(z[i], (float) 0.35, (float) 0.001, (float) 0.05);
    }

    for (int i = k; i < z.length; i++) {
      result[i] = (new Transformations()).sMulti(z[i], 30, 95, (float) 0.35);
    }

    return result;
  }

  /**
   * WFG9 t3 transformation
   */
  public float[] t3(float[] z, int k, int M) {
    float[] result = new float[M];

    for (int i = 1; i <= M - 1; i++) {
      int head = (i - 1) * k / (M - 1) + 1;
      int tail = i * k / (M - 1);
      float[] subZ = subVector(z, head - 1, tail - 1);
      result[i - 1] = (new Transformations()).rNonsep(subZ, k / (M - 1));
    }

    int head = k + 1;
    int tail = z.length;
    int l = z.length - k;
    float[] subZ = subVector(z, head - 1, tail - 1);
    result[M - 1] = (new Transformations()).rNonsep(subZ, l);

    return result;
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   * @throws org.uma.jmetal.util.JMetalException
   */
  public void evaluate(DoubleSolution solution) {
    float[] variables = new float[getNumberOfVariables()];
    double[] x = new double[getNumberOfVariables()];

    for (int i = 0; i < getNumberOfVariables(); i++) {
      x[i] = solution.getVariableValue(i);
    }

    for (int i = 0; i < getNumberOfVariables(); i++) {
      variables[i] = (float) x[i] ;
    }

    float[] sol2 = evaluate(variables);

    for (int i = 0; i < sol2.length; i++) {
      solution.setObjective(i, sol2[i]);
    }
  }
}


