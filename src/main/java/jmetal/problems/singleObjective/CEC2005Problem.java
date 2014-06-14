//  Sphere.java
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

package jmetal.problems.singleObjective;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutiontype.BinaryRealSolutionType;
import jmetal.encodings.solutiontype.RealSolutionType;
import jmetal.problems.singleObjective.cec2005Competition.originalCode.Benchmark;
import jmetal.problems.singleObjective.cec2005Competition.originalCode.TestFunc;
import jmetal.util.JMetalException;

/**
 * Class representing a Sphere problem.
 */
public class CEC2005Problem extends Problem {
  TestFunc testFunction_;
  /**
   * Constructor
   * Creates a default instance of the Sphere problem
   *
   * @param numberOfVariables Number of variables of the problem
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public CEC2005Problem(String solutionType, int problemID, int numberOfVariables)
    throws JMetalException {
    numberOfVariables_ = numberOfVariables;
    numberOfObjectives_ = 1;
    numberOfConstraints_ = 0;
    problemName_ = "CEC2005";

    Benchmark cec2005ProblemFactory = new Benchmark();
    testFunction_ = cec2005ProblemFactory.testFunctionFactory(problemID, numberOfVariables);

    upperLimit_ = new double[numberOfVariables_];
    lowerLimit_ = new double[numberOfVariables_];

    double ulimit = 0;
    double llimit = 0;

    switch (problemID) {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 14:
        ulimit = 100;
        llimit = -100;
        break;
      case 7:
      case 25:
        ulimit = Double.MAX_VALUE;
        llimit = Double.MIN_VALUE;
        break;
      case 8:
        ulimit = 32;
        llimit = -32;
        break;
      case 9:
      case 10:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
      case 24:
        ulimit = 5;
        llimit = -5;
        break;
      case 11:
        ulimit = 0.5;
        llimit = -0.5;
        break;
      case 12:
        ulimit = Math.PI;
        llimit = -Math.PI;
        break;
      case 13:
        ulimit = 3;
        llimit = 1;
        break;
      default:
        throw new JMetalException("Invalid problem value");
    }

    for (int var = 0; var < numberOfVariables_; var++) {
      lowerLimit_[var] = llimit;
      upperLimit_[var] = ulimit;
    }

    if (solutionType.compareTo("BinaryReal") == 0) {
      solutionType_ = new BinaryRealSolutionType(this);
    } else if (solutionType.compareTo("Real") == 0) {
      solutionType_ = new RealSolutionType(this);
    } else {
      throw new JMetalException("Error: solution type " + solutionType + " invalid");
    }
  }

  /**
   * Evaluates a solution
   *
   * @param solution The solution to evaluate
   * @throws jmetal.util.JMetalException
   */
  public void evaluate(Solution solution) throws JMetalException {
    Variable[] decisionVariables = solution.getDecisionVariables();
    double[] x = new double[decisionVariables.length];

    for (int i = 0; i < decisionVariables.length; i++) {
      x[i] = decisionVariables[i].getValue();
    }

    double result;
    result = testFunction_.f(x);

    solution.setObjective(0, result);
  }
}

