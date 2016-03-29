//  R2.java
//
//  Author:
//       Juan J. Durillo <juanjo.durillo@gmail.com>
//
//  Copyright (c) 2013 Juan J. Durillo
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

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.naming.impl.SimpleDescribedEntity;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * TODO: Add comments here
 */
@SuppressWarnings("serial")
public class R2<Evaluate extends List<? extends Solution<?>>>
    extends SimpleDescribedEntity
    implements QualityIndicator<Evaluate,Double> {
  private double[][] matrix = null;
  private double[][] lambda = null;
  

  private Front referenceParetoFront ;

  /**
   * Creates a new instance of the R2 indicator for a problem with
   * two objectives and 100 lambda vectors
   */
  public R2(String referenceParetoFrontFile) throws FileNotFoundException {
    // by default it creates an R2 indicator for a two dimensions problem and
    // uses only 100 weight vectors for the R2 computation
    super("R2", "R2 quality indicator") ;

    Front front = new ArrayFront(referenceParetoFrontFile);
    referenceParetoFront = front ;

  
    // generating the weights
    lambda = new double[100][2];
    for (int n = 0; n < 100; n++) {
      double a = 1.0 * n / (100 - 1);
      lambda[n][0] = a;
      lambda[n][1] = 1 - a;
    }
  }

  /**
   * Creates a new instance of the R2 indicator for a problem with
   * two objectives and 100 lambda vectors
   */
  public R2(Front referenceParetoFrontFile) throws FileNotFoundException {
    // by default it creates an R2 indicator for a two dimensions problem and
    // uses only 100 weight vectors for the R2 computation
    super("R2", "R2 quality indicator") ;

    referenceParetoFront = referenceParetoFrontFile ;

  
    // generating the weights
    lambda = new double[100][2];
    for (int n = 0; n < 100; n++) {
      double a = 1.0 * n / (100 - 1);
      lambda[n][0] = a;
      lambda[n][1] = 1 - a;
    }
  }

  /**
   * Creates a new instance of the R2 indicator for a problem with
   * two objectives and N lambda vectors
   */
  public R2(int nVectors, String referenceParetoFrontFile) throws FileNotFoundException {
    // by default it creates an R2 indicator for a two dimensions problem and
    // uses only <code>nVectors</code> weight vectors for the R2 computation
    super("R2", "R2 quality indicator") ;

    Front front = new ArrayFront(referenceParetoFrontFile);
    referenceParetoFront = front ;

  
    // generating the weights
    lambda = new double[nVectors][2];
    for (int n = 0; n < nVectors; n++) {
      double a = 1.0 * n / (nVectors - 1);
      lambda[n][0] = a;
      lambda[n][1] = 1 - a;
    }
  }

  @Override public Double evaluate(Evaluate solutionList) {
    return r2(new ArrayFront(solutionList), referenceParetoFront);
  }

  @Override public String getName() {
    return super.getName();
  }

  /**
   * Returns the R2 indicator value of a given front
   * @param front
   * @param referenceFront
   * @return
   */
  public double r2(Front front, Front referenceFront) {
    int numberOfObjectives = front.getPoint(0).getNumberOfDimensions() ;

    // STEP 3. compute all the matrix of Tschebyscheff values if it is null
    matrix = new double[front.getNumberOfPoints()][lambda.length];
    for (int i = 0; i < front.getNumberOfPoints(); i++) {
      for (int j = 0; j < lambda.length; j++) {
        matrix[i][j] = lambda[j][0] * Math.abs(front.getPoint(i).getDimensionValue(0));
        for (int n = 1; n < numberOfObjectives; n++) {
          matrix[i][j] = Math.max(matrix[i][j],
            lambda[j][n] * Math.abs(front.getPoint(i).getDimensionValue(n)));
        }
      }
    }

    double sum = 0.0;
    for (int i = 0; i < lambda.length; i++) {
      double tmp = matrix[0][i];
      for (int j = 1; j < front.getNumberOfPoints(); j++) {
        tmp = Math.min(tmp, matrix[j][i]);
      }
      sum += tmp;
    }

    return sum / (double) lambda.length;
  }
}
