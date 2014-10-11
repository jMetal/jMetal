//  LZ09_F8.java
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

package org.uma.jmetal45.problem.multiobjective.lz09;

import org.uma.jmetal45.core.Problem;
import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal45.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal45.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal45.util.JMetalException;

import java.util.ArrayList;

/**
 * Class representing problem LZ09F8
 */
public class LZ09F8 extends Problem {
  private static final long serialVersionUID = 7885765236445893586L;

  private LZ09 lz09;

  /**
   * Creates a default LZ09F8 problem (10 variables and 2 objectives)
   *
   * @param solutionType The solution type must "Real" or "BinaryReal".
   */
  public LZ09F8(String solutionType) throws ClassNotFoundException, JMetalException {
    this(solutionType, 21, 4, 21);
  }

  /**
   * Creates a LZ09F8 problem instance
   */
  public LZ09F8(String solutionType,
                Integer ptype,
                Integer dtype,
                Integer ltype) throws JMetalException {
    numberOfVariables = 10;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "LZ09F8";

    lz09 = new LZ09(numberOfVariables,
      numberOfObjectives,
      ptype,
      dtype,
      ltype);

    lowerLimit = new double[numberOfVariables];
    upperLimit = new double[numberOfVariables];
    for (int var = 0; var < numberOfVariables; var++) {
      lowerLimit[var] = 0.0;
      upperLimit[var] = 1.0;
    }

    if (solutionType.compareTo("BinaryReal") == 0) {
      this.solutionType = new BinaryRealSolutionType(this);
    } else if (solutionType.compareTo("Real") == 0) {
      this.solutionType = new RealSolutionType(this);
    } else {
      throw new JMetalException("Error: solution type " + solutionType + " invalid");
    }
  }

  /** Evaluate() method */
  public void evaluate(Solution solution) throws JMetalException {
    ArrayList<Double> x = new ArrayList<Double>(numberOfVariables);
    ArrayList<Double> y = new ArrayList<Double>(numberOfObjectives);

    XReal sol = new XReal(solution) ;
    for (int i = 0; i < numberOfVariables; i++) {
      x.add(sol.getValue(i));
      y.add(0.0);
    }

    lz09.objective(x, y);

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.setObjective(i, y.get(i));
    }
  }
}

