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

package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * This class implements the unary epsilon additive indicator as proposed in E.
 * Zitzler, E. Thiele, L. Laummanns, M., Fonseca, C., and Grunert da Fonseca. V
 * (2003): Performance Assessment of Multiobjective Optimizers: An Analysis and
 * Review. The code is the a Java version of the original metric implementation
 * by Eckart Zitzler. It can be used also as a command line program just by
 * typing $java org.uma.jmetal.qualityindicator.impl.Epsilon <solutionFrontFile>
 * <trueFrontFile> <getNumberOfObjectives>
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class Epsilon<S extends Solution<?>> extends GenericIndicator<S> {

  /**
   * Default constructor
   */
  public Epsilon() {
  }

  /**
   * Constructor
   *
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public Epsilon(String referenceParetoFrontFile) throws FileNotFoundException {
    super(referenceParetoFrontFile) ;
  }

  /**
   * Constructor
   *
   * @param referenceParetoFront
   */
  public Epsilon(Front referenceParetoFront) {
    super(referenceParetoFront) ;
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true ;
  }

  /**
   * Evaluate() method
   *
   * @param solutionList
   * @return
   */
  @Override public Double evaluate(List<S> solutionList) {
    if (solutionList == null) {
      throw new JMetalException("The pareto front approximation list is null") ;
    }

    return epsilon(new ArrayFront(solutionList), referenceParetoFront);
  }

  /**
   * Returns the value of the epsilon indicator.
   *
   * @param front Solution front
   * @param referenceFront Optimal Pareto front
   * @return the value of the epsilon indicator
   * @throws JMetalException
   */
  private double epsilon(Front front, Front referenceFront) throws JMetalException {

    double eps, epsJ = 0.0, epsK = 0.0, epsTemp;

    int numberOfObjectives = front.getPointDimensions() ;

    eps = Double.MIN_VALUE;


    for (int i = 0; i < referenceFront.getNumberOfPoints(); i++) {
      for (int j = 0; j < front.getNumberOfPoints(); j++) {
        for (int k = 0; k < numberOfObjectives; k++) {
          epsTemp = front.getPoint(j).getDimensionValue(k)
              - referenceFront.getPoint(i).getDimensionValue(k);
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

  @Override public String getName() {
    return "EP" ;
  }

  @Override public String getDescription() {
    return "Additive Epsilon quality indicator" ;
  }
}
