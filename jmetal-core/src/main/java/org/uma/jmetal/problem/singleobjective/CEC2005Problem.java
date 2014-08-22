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

package org.uma.jmetal.problem.singleobjective;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.ArrayRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.problem.singleobjective.cec2005Competition.originalCode.Benchmark;
import org.uma.jmetal.problem.singleobjective.cec2005Competition.originalCode.TestFunc;
import org.uma.jmetal.util.JMetalException;

/**
 * Class representing a Sphere problem.
 */
public class CEC2005Problem extends Problem {
  TestFunc testFunction;
  /** Constructor */
  public CEC2005Problem(String solutionType, int problemID, int numberOfVariables)
    throws JMetalException {
    this.numberOfVariables = numberOfVariables;
    numberOfObjectives = 1;
    numberOfConstraints = 0;
    problemName = "CEC2005";

    Benchmark cec2005ProblemFactory = new Benchmark();
    testFunction = cec2005ProblemFactory.testFunctionFactory(problemID, numberOfVariables);

    upperLimit = new double[this.numberOfVariables];
    lowerLimit = new double[this.numberOfVariables];

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

    for (int var = 0; var < this.numberOfVariables; var++) {
      lowerLimit[var] = llimit;
      upperLimit[var] = ulimit;
    }

    if (solutionType.compareTo("BinaryReal") == 0) {
      this.solutionType = new BinaryRealSolutionType(this);
    } else if (solutionType.compareTo("Real") == 0) {
      this.solutionType = new RealSolutionType(this);
    } else if (solutionType.compareTo("ArrayReal") == 0) {
      this.solutionType = new ArrayRealSolutionType(this);
    } else {
      throw new JMetalException("Error: solution type " + solutionType + " invalid");
    }
  }

  /** Evaluate() method */
  public void evaluate(Solution solution) throws JMetalException {
    double[] x = new double[solution.getDecisionVariables().length];

    for (int i = 0; i < solution.getDecisionVariables().length; i++) {
      x[i] = XReal.getValue(solution, i) ;
    }

    double result;
    result = testFunction.f(x);

    solution.setObjective(0, result);
  }
}

