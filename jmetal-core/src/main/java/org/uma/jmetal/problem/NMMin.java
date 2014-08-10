//  NMMin.java
//
//  Author:
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
//
package org.uma.jmetal.problem;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.IntSolutionType;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.encoding.solutiontype.wrapper.XInt;

/**
 * Created by Antonio J. Nebro on 03/07/14.
 * Bi-objective problem for testing integer encoding.
 * Objective 1: minimizing the distance to value N
 * Objective 2: minimizing the distance to value M
 */
public class NMMin extends Problem {
  private int valueN ;
  private int valueM ;

  public NMMin(String solutionType) throws ClassNotFoundException, JMetalException {
    this(solutionType, 25, 100, -100, -1000, +1000);
  }

  /** Constructor */
  public NMMin(String solutionType, int numberOfVariables, int n, int m, int lowerBound, int upperBound) throws JMetalException {
    valueN = n ;
    valueM = m ;
    this.numberOfVariables = numberOfVariables;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "NMMin";

    lowerLimit = new double[this.numberOfVariables];
    upperLimit = new double[this.numberOfVariables];
    for (int var = 0; var < this.numberOfVariables; var++) {
      lowerLimit[var] = lowerBound;
      upperLimit[var] = upperBound;
    }

    if (solutionType.compareTo("Integer") == 0) {
      this.solutionType = new IntSolutionType(this);
    } else {
      throw new JMetalException("Error: solution type " + solutionType + " invalid");
    }
  }

  /** Evaluate() method */
  public void evaluate(Solution solution) {
    int approximationToN;
    int approximationToM ;

    approximationToN = 0;
    approximationToM = 0;

    for (int i = 0; i < XInt.getNumberOfDecisionVariables(solution); i++) {
      int value = XInt.getValue(solution, i) ;
      approximationToN += Math.abs(valueN - value) ;
      approximationToM += Math.abs(valueM - value) ;
    }

    solution.setObjective(0, approximationToN);
    solution.setObjective(1, approximationToM);
  }
}
