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
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
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
   * @param solutionType      The solutiontype type must "Real" or "BinaryReal".
   */
  public Rastrigin(String solutionType, Integer numberOfVariables)
    throws ClassNotFoundException, JMetalException {
    this.numberOfVariables = numberOfVariables;
    numberOfObjectives = 1;
    numberOfConstraints = 0;
    problemName = "Rastrigin";

    upperLimit = new double[this.numberOfVariables];
    lowerLimit = new double[this.numberOfVariables];
    for (int var = 0; var < this.numberOfVariables; var++) {
      lowerLimit[var] = -5.12;
      upperLimit[var] = 5.12;
    }

    if (solutionType.compareTo("BinaryReal") == 0) {
      this.solutionType = new BinaryRealSolutionType(this);
    } else if (solutionType.compareTo("Real") == 0) {
      this.solutionType = new RealSolutionType(this);
    } else {
      throw new JMetalException("Error: solutiontype type " + solutionType + " invalid");
    }
  } // Rastrigin

  /**
   * Evaluates a solutiontype
   *
   * @param solution The solutiontype to evaluate
   * @throws org.uma.jmetal.util.JMetalException
   */
  public void evaluate(Solution solution) throws JMetalException {
    Variable[] decisionVariables = solution.getDecisionVariables();

    double result = 0.0;
    double a = 10.0;
    double w = 2 * Math.PI;

    double[] x = new double[numberOfVariables];

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = decisionVariables[i].getValue();
    }

    for (int i = 0; i < numberOfVariables; i++) {
      result += x[i] * x[i] - a * Math.cos(w * x[i]);
    }
    result += a * numberOfVariables;

    solution.setObjective(0, result);
  }
}

