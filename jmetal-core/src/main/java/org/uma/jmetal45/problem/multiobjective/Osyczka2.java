//  Osyczka2.java
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

package org.uma.jmetal45.problem.multiobjective;

import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal45.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal45.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal45.util.JMetalException;

/**
 * Class representing problem Oyczka2
 */
public class Osyczka2 extends Problem {
  private static final long serialVersionUID = 5547671401217847034L;

  /**
   * Constructor.
   * Creates a default instance of the Osyczka2 problem.
   *
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public Osyczka2(String solutionType) throws JMetalException {
    numberOfVariables = 6;
    numberOfObjectives = 2;
    numberOfConstraints = 6;
    problemName = "Osyczka2";

    lowerLimit = new double[numberOfVariables];
    upperLimit = new double[numberOfVariables];

    lowerLimit[0] = 0.0;
    lowerLimit[1] = 0.0;
    lowerLimit[2] = 1.0;
    lowerLimit[3] = 0.0;
    lowerLimit[4] = 1.0;
    lowerLimit[5] = 0.0;

    upperLimit[0] = 10.0;
    upperLimit[1] = 10.0;
    upperLimit[2] = 5.0;
    upperLimit[3] = 6.0;
    upperLimit[4] = 5.0;
    upperLimit[5] = 10.0;

    if (solutionType.compareTo("BinaryReal") == 0) {
      this.solutionType = new BinaryRealSolutionType(this);
    } else if (solutionType.compareTo("Real") == 0) {
      this.solutionType = new RealSolutionType(this);
    } else {
      throw new JMetalException("Error: solution type " + solutionType + " invalid");
    }
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   * @throws org.uma.jmetal45.util.JMetalException
   */
  public void evaluate(Solution solution) throws JMetalException {
    XReal sol = new XReal(solution) ;
    double[] f = new double[numberOfObjectives];

    double x1, x2, x3, x4, x5, x6;
    x1 = sol.getValue(0);
    x2 = sol.getValue(1);
    x3 = sol.getValue(2);
    x4 = sol.getValue(3);
    x5 = sol.getValue(4);
    x6 = sol.getValue(5);

    f[0] = -(25.0 * (x1 - 2.0) * (x1 - 2.0) +
      (x2 - 2.0) * (x2 - 2.0) +
      (x3 - 1.0) * (x3 - 1.0) +
      (x4 - 4.0) * (x4 - 4.0) +
      (x5 - 1.0) * (x5 - 1.0));

    f[1] = x1 * x1 + x2 * x2 + x3 * x3 + x4 * x4 + x5 * x5 + x6 * x6;

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }

  /** Evaluate() method */
  public void evaluateConstraints(Solution solution) throws JMetalException {
    double[] constraint = new double[this.getNumberOfConstraints()];
    XReal sol = new XReal(solution) ;

    double x1, x2, x3, x4, x5, x6;
    x1 = sol.getValue(0);
    x2 = sol.getValue(1);
    x3 = sol.getValue(2);
    x4 = sol.getValue(3);
    x5 = sol.getValue(4);
    x6 = sol.getValue(5);

    constraint[0] = (x1 + x2) / 2.0 - 1.0;
    constraint[1] = (6.0 - x1 - x2) / 6.0;
    constraint[2] = (2.0 - x2 + x1) / 2.0;
    constraint[3] = (2.0 - x1 + 3.0 * x2) / 2.0;
    constraint[4] = (4.0 - (x3 - 3.0) * (x3 - 3.0) - x4) / 4.0;
    constraint[5] = ((x5 - 3.0) * (x5 - 3.0) + x6 - 4.0) / 4.0;

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

