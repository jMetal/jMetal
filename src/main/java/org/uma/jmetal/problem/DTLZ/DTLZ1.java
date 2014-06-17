//  DTLZ1.java
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

package org.uma.jmetal.problem.DTLZ;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.encoding.solution.BinaryRealSolution;
import org.uma.jmetal.encoding.solution.RealSolution;
import org.uma.jmetal.util.JMetalException;

/**
 * Class representing problem DTLZ1
 */
public class DTLZ1 extends Problem {
  /**
   *
   */
  private static final long serialVersionUID = -3716448671122961793L;

  /**
   * Creates a default DTLZ1 problem (7 variables and 3 objectives)
   *
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public DTLZ1(String solutionType) throws ClassNotFoundException, JMetalException {
    this(solutionType, 7, 3);
  } // DTLZ1   

  /**
   * Creates a DTLZ1 problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   * @param solutionType       The solution type must "Real" or "BinaryReal".
   */
  public DTLZ1(String solutionType,
    Integer numberOfVariables,
    Integer numberOfObjectives) throws JMetalException {
    numberOfVariables_ = numberOfVariables;
    numberOfObjectives_ = numberOfObjectives;
    numberOfConstraints_ = 0;
    problemName_ = "DTLZ1";

    lowerLimit_ = new double[numberOfVariables_];
    upperLimit_ = new double[numberOfVariables_];
    for (int var = 0; var < numberOfVariables; var++) {
      lowerLimit_[var] = 0.0;
      upperLimit_[var] = 1.0;
    }

    if (solutionType.compareTo("BinaryReal") == 0) {
      solutionType_ = new BinaryRealSolution(this);
    } else if (solutionType.compareTo("Real") == 0) {
      solutionType_ = new RealSolution(this);
    } else {
      throw new JMetalException("Error: solution type " + solutionType + " invalid");
    }
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   * @throws org.uma.jmetal.util.JMetalException
   */
  public void evaluate(Solution solution) throws JMetalException {
    Variable[] gen = solution.getDecisionVariables();

    double[] x = new double[numberOfVariables_];
    double[] f = new double[numberOfObjectives_];
    int k = numberOfVariables_ - numberOfObjectives_ + 1;

    for (int i = 0; i < numberOfVariables_; i++) {
      x[i] = gen[i].getValue();
    }

    double g = 0.0;
    for (int i = numberOfVariables_ - k; i < numberOfVariables_; i++) {
      g += (x[i] - 0.5) * (x[i] - 0.5) - Math.cos(20.0 * Math.PI * (x[i] - 0.5));
    }

    g = 100 * (k + g);
    for (int i = 0; i < numberOfObjectives_; i++) {
      f[i] = (1.0 + g) * 0.5;
    }

    for (int i = 0; i < numberOfObjectives_; i++) {
      for (int j = 0; j < numberOfObjectives_ - (i + 1); j++) {
        f[i] *= x[j];
      }
      if (i != 0) {
        int aux = numberOfObjectives_ - (i + 1);
        f[i] *= 1 - x[aux];
      }
    }

    for (int i = 0; i < numberOfObjectives_; i++) {
      solution.setObjective(i, f[i]);
    }
  }
}

