//  WFG1.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.problem.multiobjective.wfg;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;

import java.util.logging.Level;

/**
 * This class implements the WFG1 problem
 * Reference: Simon Huband, Luigi Barone, Lyndon While, Phil Hingston
 * A Scalable Multi-objective Test Problem Toolkit.
 * Evolutionary Multi-Criterion Optimization:
 * Third International Conference, EMO 2005.
 * Proceedings, volume 3410 of Lecture Notes in Computer Science
 */
public class WFG1 extends WFG {
  /**
   * Constructor
   * Creates a default WFG1 instance with
   * 2 position-related parameters
   * 4 distance-related parameters
   * and 2 objectives
   */
  public WFG1() throws ClassNotFoundException, JMetalException {
    this(2, 4, 2);
  }

  /**
   * Creates a WFG1 problem instance
   *
   * @param k            Number of position parameters
   * @param l            Number of distance parameters
   * @param m            Number of objective functions
   */
  public WFG1(Integer k, Integer l, Integer m)
    throws ClassNotFoundException, JMetalException {
    super(k, l, m);
    setName("WFG1");

    s = new int[m];
    for (int i = 0; i < m; i++) {
      s[i] = 2 * (i + 1);
    }

    a = new int[m - 1];
    for (int i = 0; i < m - 1; i++) {
      a[i] = 1;
    }
  }

  /** Evaluate */
  public float[] evaluate(float[] z) {
    float[] y;

    y = normalise(z);
    y = t1(y, k);
    y = t2(y, k);
    try {
      y = t3(y);
    } catch (JMetalException e) {
      JMetalLogger.logger.log(Level.SEVERE, "Error", e);
    }
    y = t4(y, k, m);


    float[] result = new float[m];
    float[] x = calculateX(y);
    for (int m = 1; m <= this.m - 1; m++) {
      result[m - 1] = d * x[this.m - 1] + s[m - 1] * (new Shapes()).convex(x, m);
    }

    result[m - 1] = d * x[m - 1] + s[m - 1] * (new Shapes()).mixed(x, 5, (float) 1.0);

    return result;
  }

  /**
   * WFG1 t1 transformation
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
   * WFG1 t2 transformation
   */
  public float[] t2(float[] z, int k) {
    float[] result = new float[z.length];

    System.arraycopy(z, 0, result, 0, k);

    for (int i = k; i < z.length; i++) {
      result[i] = (new Transformations()).bFlat(z[i], (float) 0.8, (float) 0.75, (float) 0.85);
    }

    return result;
  }

  /**
   * WFG1 t3 transformation
   *
   * @throws org.uma.jmetal.util.JMetalException
   */
  public float[] t3(float[] z) throws JMetalException {
    float[] result = new float[z.length];

    for (int i = 0; i < z.length; i++) {
      result[i] = (new Transformations()).bPoly(z[i], (float) 0.02);
    }

    return result;
  }

  /**
   * WFG1 t4 transformation
   */
  public float[] t4(float[] z, int k, int M) {
    float[] result = new float[M];
    float[] w = new float[z.length];

    for (int i = 0; i < z.length; i++) {
      w[i] = (float) 2.0 * (i + 1);
    }

    for (int i = 1; i <= M - 1; i++) {
      int head = (i - 1) * k / (M - 1) + 1;
      int tail = i * k / (M - 1);
      float[] subZ = subVector(z, head - 1, tail - 1);
      float[] subW = subVector(w, head - 1, tail - 1);

      result[i - 1] = (new Transformations()).rSum(subZ, subW);
    }

    int head = k + 1 - 1;
    int tail = z.length - 1;
    float[] subZ = subVector(z, head, tail);
    float[] subW = subVector(w, head, tail);
    result[M - 1] = (new Transformations()).rSum(subZ, subW);

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

    float[] f = evaluate(variables);

    for (int i = 0; i < f.length; i++) {
      solution.setObjective(i, f[i]);
    }
  }
}
