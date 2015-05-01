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

package org.uma.jmetal.problem.multiobjective.wfg;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Implements a reference abstract class for all wfg org.uma.test problem
 * Reference: Simon Huband, Luigi Barone, Lyndon While, Phil Hingston
 * A Scalable Multi-objective Test Problem Toolkit.
 * Evolutionary Multi-Criterion Optimization:
 * Third International Conference, EMO 2005.
 * Proceedings, volume 3410 of Lecture Notes in Computer Science
 */
public abstract class WFG extends AbstractDoubleProblem {

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
   */
  public WFG(Integer k, Integer l, Integer M) {
    this.k = k;
    this.l = l;
    this.m = M;

    setNumberOfVariables(this.k + this.l);
    setNumberOfObjectives(this.m);
    setNumberOfConstraints(0);

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(0.0);
      upperLimit.add(2.0*(i+1));
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  @Override
  public DoubleSolution createSolution() {
    return new DefaultDoubleSolution(this)  ;
  }

  /**
   * Gets the x vector
   */
  public float[] calculateX(float[] t) {
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
      result[i] = correctTo01(result[i]);
    }

    return result;
  }

  /**
   */
  public float correctTo01(float a) {
    float min = (float) 0.0;
    float max = (float) 1.0;

    float minEpsilon = min - epsilon;
    float maxEpsilon = max + epsilon;

    if ((a <= min && a >= minEpsilon) || (a >= min && a <= minEpsilon)) {
      return min;
    } else if ((a >= max && a <= maxEpsilon) || (a <= max && a >= maxEpsilon)) {
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
   * Evaluates a solution
   *
   * @param variables The solution to evaluate
   * @return a double [] with the evaluation results
   */
  abstract public float[] evaluate(float[] variables);
}
