//  Epsilon.java
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

package org.uma.jmetal.qualityIndicator;

import org.uma.jmetal.qualityIndicator.util.MetricsUtil;
import org.uma.jmetal.util.JMetalException;

/**
 * This class implements the unary epsilon additive indicator as proposed in E.
 * Zitzler, E. Thiele, L. Laummanns, M., Fonseca, C., and Grunert da Fonseca. V
 * (2003): Performance Assessment of Multiobjective Optimizers: An Analysis and
 * Review. The code is the a Java version of the original metric implementation
 * by Eckart Zitzler. It can be used also as a command line program just by
 * typing $java org.uma.jmetal.qualityIndicator.Epsilon <solutionFrontFile>
 * <trueFrontFile> <getNumberOfObjectives>
 */

public class Epsilon implements QualityIndicator {
  private static final String NAME = "EPSILON" ;
  private MetricsUtil utils = new MetricsUtil();

  int numberOfObjectives;
  /*
   * obj[i]=0 means objective i is to be minimized. This code always assume the
   * minimization of all the objectives
   */
  int[] obj; /* obj[i] = 0 means objective i is to be minimized */
  /*
   * method = 0 means apply additive epsilon and method = 1 means
   * multiplicative epsilon. This code always apply additive epsilon
   */
  int method;

  /**
   * Returns the epsilon indicator.
   *
   * @param a Solution front
   * @param b True Pareto front
   * @return the value of the epsilon indicator
   * @throws org.uma.jmetal.util.JMetalException
   */
  public double epsilon(double[][] a, double[][] b) throws JMetalException {
    int i, j, k;
    double eps, epsJ = 0.0, epsK = 0.0, epsTemp;

    this.numberOfObjectives = a[0].length;
    setParams();

    if (method == 0) {
      eps = Double.MIN_VALUE;
    } else {
      eps = 0;
    }

    for (i = 0; i < a.length; i++) {
      for (j = 0; j < b.length; j++) {
        for (k = 0; k < this.numberOfObjectives; k++) {
          switch (method) {
            case 0:
              if (obj[k] == 0) {
                epsTemp = b[j][k] - a[i][k];
              } else {
                epsTemp = a[i][k] - b[j][k];
              }
              break;
            default:
              if ((a[i][k] < 0 && b[j][k] > 0) || (a[i][k] > 0 && b[j][k] < 0)
                || (a[i][k] == 0 || b[j][k] == 0)) {
                throw new JMetalException("Error in data file");
              }
              if (obj[k] == 0) {
                epsTemp = b[j][k] / a[i][k];
              } else {
                epsTemp = a[i][k] / b[j][k];
              }
              break;
          }
          if (k == 0) {
            epsK = epsTemp;
          } else if (epsK < epsTemp) {
            epsK = epsTemp;
          }
        }
        if (j == 0) {
          epsJ = epsK;
        } else if (epsJ > epsK) {
          epsJ = epsK;
        }
      }
      if (i == 0) {
        eps = epsJ;
      } else if (eps < epsJ) {
        eps = epsJ;
      }
    }
    return eps;
  }

  /**
   * Established the params by default
   */
  void setParams() {
    int i;
    obj = new int[numberOfObjectives];
    for (i = 0; i < numberOfObjectives; i++) {
      obj[i] = 0;
    }
    method = 0;
  }

  @Override
  public double execute(double[][] paretoFrontApproximation, double[][] paretoTrueFront) {
    return epsilon(paretoFrontApproximation, paretoTrueFront) ;
  }

  @Override public String getName() {
    return NAME ;
  }
}
