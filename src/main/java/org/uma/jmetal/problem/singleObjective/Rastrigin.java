//  Rastrigin.java
//
//  Author:
//       Esteban López-Camacho <esteban@lcc.uma.es>
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

package org.uma.jmetal.problem.singleObjective;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.encoding.solution.BinaryRealSolution;
import org.uma.jmetal.encoding.solution.RealSolution;
import org.uma.jmetal.util.JMetalException;

public class Rastrigin extends Problem {

  /**
   *
   */
  private static final long serialVersionUID = -6521581306133870406L;

  /**
   * Constructor
   * Creates a default instance of the Rastrigin problem
   *
   * @param numberOfVariables Number of variables of the problem
   * @param solutionType      The solution type must "Real" or "BinaryReal".
   */
  public Rastrigin(String solutionType, Integer numberOfVariables)
    throws ClassNotFoundException, JMetalException {
    numberOfVariables_ = numberOfVariables;
    numberOfObjectives_ = 1;
    numberOfConstraints_ = 0;
    problemName_ = "Rastrigin";

    upperLimit_ = new double[numberOfVariables_];
    lowerLimit_ = new double[numberOfVariables_];
    for (int var = 0; var < numberOfVariables_; var++) {
      lowerLimit_[var] = -5.12;
      upperLimit_[var] = 5.12;
    }

    if (solutionType.compareTo("BinaryReal") == 0) {
      solutionType_ = new BinaryRealSolution(this);
    } else if (solutionType.compareTo("Real") == 0) {
      solutionType_ = new RealSolution(this);
    } else {
      throw new JMetalException("Error: solution type " + solutionType + " invalid");
    }
  } // Rastrigin

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   * @throws org.uma.jmetal.util.JMetalException
   */
  public void evaluate(Solution solution) throws JMetalException {
    Variable[] decisionVariables = solution.getDecisionVariables();

    double result = 0.0;
    double a = 10.0;
    double w = 2 * Math.PI;

    double[] x = new double[numberOfVariables_];

    for (int i = 0; i < numberOfVariables_; i++) {
      x[i] = decisionVariables[i].getValue();
    }

    for (int i = 0; i < numberOfVariables_; i++) {
      result += x[i] * x[i] - a * Math.cos(w * x[i]);
    }
    result += a * numberOfVariables_;

    solution.setObjective(0, result);
  }
}

