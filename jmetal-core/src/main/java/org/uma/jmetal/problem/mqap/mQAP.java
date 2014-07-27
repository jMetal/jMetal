//  mQAP.java
//
//  Author:
//       Juan J. Durillo <juan@dps.uibk.ac.at>
//
//  Copyright (c) 2011 Juan J. Durillo
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

package org.uma.jmetal.problem.mqap;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.PermutationSolutionType;
import org.uma.jmetal.encoding.variable.Permutation;
import org.uma.jmetal.util.JMetalException;

/**
 * @author Juan J. Durillo
 * @version 1.0
 *          This class implements the mQAP problem.
 *          Please notice that this class is also valid for the case m = 1 (mono-objective
 *          version of the problem)
 */
public class mQAP extends Problem {
  private static final long serialVersionUID = 5585099487667493237L;

  int[][] matrixA;
  int[][][] matricesB;

  public mQAP(String solutionType) throws JMetalException {
    this(solutionType, "KC10-2fl-2rl.dat");
  }

  /**
   * Creates a new instance of problem mQAP.
   */
  public mQAP(String solutionType, String fileName) throws JMetalException {

    ReadInstance ri = new ReadInstance(fileName);
    ri.loadInstance();
    numberOfVariables = 1;
    numberOfObjectives = ri.getNumberOfObjectives();
    numberOfConstraints = 0;
    problemName = "mQAP";
    matrixA = ri.get_a_Matrix();
    matricesB = ri.get_b_Matrixs();

    upperLimit = new double[numberOfVariables];
    lowerLimit = new double[numberOfVariables];

    // Establishes upper and lower limits for the variables
    for (int var = 0; var < numberOfVariables; var++) {
      lowerLimit[var] = 0.0;
      upperLimit[var] = ri.getNumberOfFacilities() - 1;
    }

    // Establishes the length of every encoding.variable
    length = new int[numberOfVariables];
    for (int var = 0; var < numberOfVariables; var++) {
      length[var] = ri.getNumberOfFacilities();
    }

    if (solutionType.compareTo("Permutation") == 0) {
      this.solutionType = new PermutationSolutionType(this);
    } else {
      throw new JMetalException("Error: solutiontype type " + solutionType + " invalid");
    }
  }

  /** Evaluate() method */
  public void evaluate(Solution solution) throws JMetalException {
    int[] permutation = ((Permutation) solution.getDecisionVariables()[0]).getVector();
    for (int k = 0; k < numberOfObjectives; k++) {
      double aux = 0.0;
      for (int i = 0; i < matrixA.length; i++) {
        for (int j = 0; j < matrixA[i].length; j++) {
          aux += matrixA[i][j] * matricesB[k][permutation[i]][permutation[j]];
        }
      }
      solution.setObjective(k, aux);
    }
  }
}
