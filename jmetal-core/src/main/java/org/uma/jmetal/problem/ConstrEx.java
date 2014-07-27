//  ConstrEx.java
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
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.util.JMetalException;

/** Class representing problem Constr_Ex */
public class ConstrEx extends Problem {
  private static final long serialVersionUID = 1790825694174189801L;

  /**
   * Constructor
   * Creates a default instance of the Constr_Ex problem
   *
   * @param solutionType The solution type type must "Real" or "BinaryReal".
   */
  public ConstrEx(String solutionType) throws JMetalException {
    numberOfVariables = 2;
    numberOfObjectives = 2;
    numberOfConstraints = 2;
    problemName = "Constr_Ex";

    lowerLimit = new double[numberOfVariables];
    upperLimit = new double[numberOfVariables];
    lowerLimit[0] = 0.1;
    lowerLimit[1] = 0.0;
    upperLimit[0] = 1.0;
    upperLimit[1] = 5.0;

    if (solutionType.compareTo("BinaryReal") == 0) {
      this.solutionType = new BinaryRealSolutionType(this);
    } else if (solutionType.compareTo("Real") == 0) {
      this.solutionType = new RealSolutionType(this);
    } else {
      throw new JMetalException("Error: solutiontype type " + solutionType + " invalid");
    }
  }

  /** Evaluate() method */
  public void evaluate(Solution solution) throws JMetalException {
    Variable[] variable = solution.getDecisionVariables();

    double[] f = new double[numberOfObjectives];
    f[0] = variable[0].getValue();
    f[1] = (1.0 + variable[1].getValue()) / variable[0].getValue();

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }

  /**
   * Evaluates the constraint overhead of a solutiontype
   *
   * @param solution The solutiontype
   * @throws org.uma.jmetal.util.JMetalException
   */
  public void evaluateConstraints(Solution solution) throws JMetalException {
    double[] constraint = new double[this.getNumberOfConstraints()];

    double x1 = solution.getDecisionVariables()[0].getValue();
    double x2 = solution.getDecisionVariables()[1].getValue();

    constraint[0] = (x2 + 9 * x1 - 6.0);
    constraint[1] = (-x2 + 9 * x1 - 1.0);

    double total = 0.0;
    int number = 0;
    for (int i = 0; i < this.getNumberOfConstraints(); i++) {
      if (constraint[i] < 0.0) {
        total += constraint[i];
        number++;
      }
    }

    solution.setOverallConstraintViolation(total);
    solution.setNumberOfViolatedConstraint(number);
  }
}
