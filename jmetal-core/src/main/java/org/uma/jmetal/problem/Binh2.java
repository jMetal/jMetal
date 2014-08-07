//  Binh2.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2012 Antonio J. Nebro
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
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;

/** Class representing problem Binh2 */
public class Binh2 extends Problem {
  private static final long serialVersionUID = 8733721399158738615L;

  /**
   * Constructor
   * Creates a default instance of the Binh2 problem
   *
   * @param solutionType The solution type type must "Real" or "BinaryReal".
   */
  public Binh2(String solutionType) throws JMetalException {
    numberOfVariables = 2;
    numberOfObjectives = 2;
    numberOfConstraints = 2;
    problemName = "Binh2";

    lowerLimit = new double[numberOfVariables];
    upperLimit = new double[numberOfVariables];
    lowerLimit[0] = 0.0;
    lowerLimit[1] = 0.0;
    upperLimit[0] = 5.0;
    upperLimit[1] = 3.0;

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
    XReal vars = new XReal(solution);

    double[] fx = new double[2];
    double[] x = new double[numberOfVariables];
    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = vars.getValue(i);
    }

    fx[0] = 4.0 * x[0] * x[0] + 4 * x[1] * x[1];
    fx[1] = (x[0] - 5.0) * (x[0] - 5.0) + (x[1] - 5.0) * (x[1] - 5.0);

    solution.setObjective(0, fx[0]);
    solution.setObjective(1, fx[1]);
  }

  /**
   * Evaluates the constraint overhead of a solution
   *
   * @param solution The solutiontype
   * @throws org.uma.jmetal.util.JMetalException
   */
  public void evaluateConstraints(Solution solution) throws JMetalException {
    double[] constraint = new double[this.getNumberOfConstraints()];

    double x0 = solution.getDecisionVariables()[0].getValue();
    double x1 = solution.getDecisionVariables()[1].getValue();

    constraint[0] = -1.0 * (x0 - 5) * (x0 - 5) - x1 * x1 + 25.0;
    constraint[1] = (x0 - 8) * (x0 - 8) + (x1 + 3) * (x1 + 3) - 7.7;

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
