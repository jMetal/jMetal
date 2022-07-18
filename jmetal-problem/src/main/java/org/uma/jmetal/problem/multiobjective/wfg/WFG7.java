package org.uma.jmetal.problem.multiobjective.wfg;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.Arrays;
import java.util.stream.IntStream;

@SuppressWarnings("serial")
public class WFG7 extends WFG {
  /**
   * Creates a default WFG7 problem with
   * 2 position-related parameters,
   * 4 distance-related parameters,
   * and 2 objectives
   */
  public WFG7() {
    this(2, 4, 2);
  }

  /**
   * Creates a WFG7 problem instance
   *
   * @param k            Number of position parameters
   * @param l            Number of distance parameters
   * @param m            Number of objective functions
   */
  public WFG7(Integer k, Integer l, Integer m) {
    super(k, l, m);
    setName("WFG7");

    s = new int[m];
    for (var i = 0; i < m; i++) {
      s[i] = 2 * (i + 1);
    }

    a = new int[m - 1];
    for (var i = 0; i < m - 1; i++) {
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

    var result = new float[m];
    var x = calculateX(y);
    for (var m = 1; m <= this.m; m++) {
      result[m - 1] = d * x[this.m - 1] + s[m - 1] * (new Shapes()).concave(x, m);
    }

    return result;
  }

  /**
   * WFG7 t1 transformation
   */
  public float[] t1(float[] z, int k) {
    var result = new float[z.length];
    var w = new float[z.length];

    for (var i = 0; i < w.length; i++) {
      w[i] = 1;
    }

    for (var i = 0; i < k; i++) {
      var head = i + 1;
      var tail = z.length - 1;
      var subZ = subVector(z, head, tail);
      var subW = subVector(w, head, tail);
      var aux = (new Transformations()).rSum(subZ, subW);

      result[i] = (new Transformations())
        .bParam(z[i], aux, (float) 0.98 / (float) 49.98, (float) 0.02, (float) 50);
    }

    System.arraycopy(z, k, result, k, z.length - k);

    return result;
  }

  /**
   * WFG7 t2 transformation
   */
  public float[] t2(float[] z, int k) {
    var result = new float[z.length];

    System.arraycopy(z, 0, result, 0, k);

    for (var i = k; i < z.length; i++) {
      result[i] = (new Transformations()).sLinear(z[i], (float) 0.35);
    }

    return result;
  }

  /**
   * WFG7 t3 transformation
   */
  public float[] t3(float[] z, int k, int M) {
    var result = new float[M];
    var w = new float[z.length];

    for (var i = 0; i < z.length; i++) {
      w[i] = (float) 1.0;
    }

    for (var i = 1; i <= M - 1; i++) {
      var head = (i - 1) * k / (M - 1) + 1;
      var tail = i * k / (M - 1);
      var subZ = subVector(z, head - 1, tail - 1);
      var subW = subVector(w, head - 1, tail - 1);

      result[i - 1] = (new Transformations()).rSum(subZ, subW);
    }

    var head = k + 1;
    var tail = z.length;
    var subZ = subVector(z, head - 1, tail - 1);
    var subW = subVector(w, head - 1, tail - 1);
    result[M - 1] = (new Transformations()).rSum(subZ, subW);

    return result;
  }

  /** Evaluate() method */
  public DoubleSolution evaluate(@NotNull DoubleSolution solution) {
    var variables = new float[getNumberOfVariables()];
    var x = new double[10];
    var count = 0;
    var bound = getNumberOfVariables();
      for (var i1 = 0; i1 < bound; i1++) {
          double v = solution.variables().get(i1);
          if (x.length == count) x = Arrays.copyOf(x, count * 2);
          x[count++] = v;
      }
      x = Arrays.copyOfRange(x, 0, count);

      for (var i = 0; i < getNumberOfVariables(); i++) {
      variables[i] = (float) x[i] ;
    }

    var sol2 = evaluate(variables);

    for (var i = 0; i < sol2.length; i++) {
      solution.objectives()[i] = sol2[i];
    }
    return solution ;
  }
}

