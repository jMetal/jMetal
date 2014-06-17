//  Tanaka.java
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

package org.uma.jmetal.problem;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.encoding.solution.BinaryRealSolution;
import org.uma.jmetal.encoding.solution.RealSolution;
import org.uma.jmetal.util.JMetalException;

/**
 * Class representing problem Tanaka
 */
public class Tanaka extends Problem {

  /**
   *
   */
  private static final long serialVersionUID = 6305140494344001015L;

  /**
   * Constructor.
   * Creates a default instance of the problem Tanaka
   *
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public Tanaka(String solutionType) throws JMetalException {
    numberOfVariables_ = 2;
    numberOfObjectives_ = 2;
    numberOfConstraints_ = 2;
    problemName_ = "Tanaka";

    upperLimit_ = new double[numberOfVariables_];
    lowerLimit_ = new double[numberOfVariables_];
    for (int var = 0; var < numberOfVariables_; var++) {
      lowerLimit_[var] = 10e-5;
      upperLimit_[var] = Math.PI;
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
    Variable[] variable = solution.getDecisionVariables();

    double[] f = new double[numberOfObjectives_];
    f[0] = variable[0].getValue();
    f[1] = variable[1].getValue();

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }


  /**
   * Evaluates the constraint overhead of a solution
   *
   * @param solution The solution
   * @throws org.uma.jmetal.util.JMetalException
   */
  public void evaluateConstraints(Solution solution) throws JMetalException {
    double[] constraint = new double[this.getNumberOfConstraints()];

    double x1 = solution.getDecisionVariables()[0].getValue();
    double x2 = solution.getDecisionVariables()[1].getValue();

    constraint[0] = (x1 * x1 + x2 * x2 - 1.0 - 0.1 * Math.cos(16.0 * Math.atan(x1 / x2)));
    constraint[1] = -2.0 * ((x1 - 0.5) * (x1 - 0.5) + (x2 - 0.5) * (x2 - 0.5) - 0.5);

    int number = 0;
    double total = 0.0;
    for (int i = 0; i < this.getNumberOfConstraints(); i++) {
      if (constraint[i] < 0.0) {
        number++;
        total += constraint[i];
      }
    }

    solution.setOverallConstraintViolation(total);
    solution.setNumberOfViolatedConstraint(number);
  }
}
