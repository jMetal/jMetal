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
  //public static enum Type {ADDITIVE, MULTIPLICATIVE} ;

  private static String NAME ;
  private MetricsUtil utils = new MetricsUtil();

  /*
   * method = 0 means apply additive epsilon and method = 1 means
   * multiplicative epsilon. The implementation assumes always minimization of objectives
   */
  //Type method;

  /** Constructor */
  public Epsilon() {
    //method = Type.ADDITIVE ;
    NAME = "EPSILON" ;
  }

  /** Constructor */
/*
  public Epsilon(Type method) {
    // TODO: check paramter values
    //if ((method != 0) && (method != 1)) {
    //  throw new JMetalException("Invalid Epsilon variant: must be 0 (additive) or 1" +
    //    "multiplicative. Received: " + method) ;
    //}
    this.method = method ;
    if (method == Type.ADDITIVE) {
      NAME = "EPSILON" ;
    } else {
      NAME = "MEPSILON" ;
    }
  }
*/

  /**
   * Returns the epsilon indicator.
   *
   * @param front Solution front
   * @param referenceFront True Pareto front
   * @return the value of the epsilon indicator
   * @throws org.uma.jmetal.util.JMetalException
   */
  public double epsilon(double[][] front, double[][] referenceFront) throws JMetalException {
    int i, j, k;
    double eps, epsJ = 0.0, epsK = 0.0, epsTemp;

    int numberOfObjectives = front[0].length;

    eps = Double.MIN_VALUE;

    for (i = 0; i < referenceFront.length; i++) {
      for (j = 0; j < front.length; j++) {
        for (k = 0; k < numberOfObjectives; k++) {
          epsTemp = front[j][k] - referenceFront[i][k];
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

  @Override
  public double execute(double[][] paretoFrontApproximation, double[][] paretoTrueFront) {
    return epsilon(paretoFrontApproximation, paretoTrueFront) ;
  }

  @Override public String getName() {
    return NAME ;
  }
}
