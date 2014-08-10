//  WFG.java
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

package org.uma.jmetal.problem.wfg;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.util.JMetalException;

import java.util.Random;

/**
 * Implements a reference abstract class for all wfg org.uma.test problem
 * Reference: Simon Huband, Luigi Barone, Lyndon While, Phil Hingston
 * A Scalable Multi-objective Test Problem Toolkit.
 * Evolutionary Multi-Criterion Optimization:
 * Third International Conference, EMO 2005.
 * Proceedings, volume 3410 of Lecture Notes in Computer Science
 */
public abstract class WFG extends Problem {
  private static final long serialVersionUID = 2252390688690699199L;

  /**
   * stores a epsilon default value
   */
  private final float epsilon = (float) 1e-7;

  protected int k;
  protected int m;
  protected int l;
  protected int[] a;
  protected int[] s;
  protected int d = 1;
  protected Random random = new Random();

  /**
   * Constructor
   * Creates a wfg problem
   *
   * @param k            position-related parameters
   * @param l            distance-related parameters
   * @param M            Number of objectives
   * @param solutionType The solutiontype type must "Real" or "BinaryReal".
   */
  public WFG(String solutionType, Integer k, Integer l, Integer M) throws JMetalException {
    this.k = k;
    this.l = l;
    this.m = M;
    numberOfVariables = this.k + this.l;
    numberOfObjectives = this.m;
    numberOfConstraints = 0;

    lowerLimit = new double[numberOfVariables];
    upperLimit = new double[numberOfVariables];
    for (int var = 0; var < numberOfVariables; var++) {
      lowerLimit[var] = 0;
      upperLimit[var] = 2 * (var + 1);
    }

    if (solutionType.compareTo("BinaryReal") == 0) {
      this.solutionType = new BinaryRealSolutionType(this);
    } else if (solutionType.compareTo("Real") == 0) {
      this.solutionType = new RealSolutionType(this);
    } else {
      throw new JMetalException("Error: solution type " + solutionType + " invalid");
    }
  }

  /**
   * Gets the x vector (consulte wfg tooltik reference)
   */
  public float[] calculate_x(float[] t) {
    float[] x = new float[m];

    for (int i = 0; i < m - 1; i++) {
      x[i] = Math.max(t[m - 1], a[i]) * (t[i] - (float) 0.5) + (float) 0.5;
    }

    x[m - 1] = t[m - 1];

    return x;
  }

  /**
   * Normalizes a vector (consulte wfg toolkit reference)
   */
  public float[] normalise(float[] z) {
    float[] result = new float[z.length];

    for (int i = 0; i < z.length; i++) {
      float bound = (float) 2.0 * (i + 1);
      result[i] = z[i] / bound;
      result[i] = correct_to_01(result[i]);
    }

    return result;
  }

  /**
   */
  public float correct_to_01(float a) {
    float min = (float) 0.0;
    float max = (float) 1.0;

    float min_epsilon = min - epsilon;
    float max_epsilon = max + epsilon;

    if ((a <= min && a >= min_epsilon) || (a >= min && a <= min_epsilon)) {
      return min;
    } else if ((a >= max && a <= max_epsilon) || (a <= max && a >= max_epsilon)) {
      return max;
    } else {
      return a;
    }
  }

  /**
   * Gets a subvector of a given vector
   * (Head inclusive and tail inclusive)
   *
   * @param z the vector
   * @return the subvector
   */
  public float[] subVector(float[] z, int head, int tail) {
    int size = tail - head + 1;
    float[] result = new float[size];

    System.arraycopy(z, head, result, head - head, tail + 1 - head);

    return result;
  }

  /**
   * Evaluates a solutiontype
   *
   * @param variables The solutiontype to evaluate
   * @return a double [] with the evaluation results
   */
  abstract public float[] evaluate(float[] variables);
}
