//  LZ09_F1.java
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

package org.uma.jmetal.problem.LZ09;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.Variable;
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.util.JMetalException;

import java.util.Vector;

/**
 * Class representing problem LZ09_F1
 */
public class LZ09_F1 extends Problem {
  /**
   *
   */
  private static final long serialVersionUID = 8764504064100559113L;
  LZ09 LZ09_;

  /**
   * Creates a default LZ09_F1 problem (30 variables and 2 objectives)
   *
   * @param solutionType The solutiontype type must "Real" or "BinaryReal".
   */
  public LZ09_F1(String solutionType) throws ClassNotFoundException, JMetalException {
    this(solutionType, 21, 1, 21);
  } // LZ09_F1

  /**
   * Creates a LZ09_F1 problem instance
   *
   * @param solutionType The solutiontype type must "Real" or "BinaryReal".
   */
  public LZ09_F1(String solutionType,
    Integer ptype,
    Integer dtype,
    Integer ltype) throws JMetalException {
    numberOfVariables = 10;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "LZ09_F1";

    LZ09_ = new LZ09(numberOfVariables,
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
      throw new JMetalException("Error: solutiontype type " + solutionType + " invalid");
    }
  }

  /**
   * Evaluates a solutiontype
   *
   * @param solution The solutiontype to evaluate
   * @throws org.uma.jmetal.util.JMetalException
   */
  public void evaluate(Solution solution) throws JMetalException {
    Variable[] gen = solution.getDecisionVariables();

    Vector<Double> x = new Vector<Double>(numberOfVariables);
    Vector<Double> y = new Vector<Double>(numberOfObjectives);

    for (int i = 0; i < numberOfVariables; i++) {
      x.addElement(gen[i].getValue());
      y.addElement(0.0);
    } // for

    LZ09_.objective(x, y);

    for (int i = 0; i < numberOfObjectives; i++) {
      solution.setObjective(i, y.get(i));
    }
  }
}
