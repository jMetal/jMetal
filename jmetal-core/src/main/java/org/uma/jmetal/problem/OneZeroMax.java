//  OneZeroMax.java
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
//  along with this program.  If not, see <http://www.gnu.org/licenses/>. * OneZeroMax.java

package org.uma.jmetal.problem;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.BinarySolutionType;
import org.uma.jmetal.encoding.variable.Binary;
import org.uma.jmetal.util.JMetalException;

/**
 * Class representing problem OneZeroMax. The problem consist of maximizing the
 * number of '1's and '0's in a binary string.
 */
public class OneZeroMax extends Problem {

  /**
   *
   */
  private static final long serialVersionUID = 2580449794690931406L;

  /**
   * Creates a new OneZeroMax problem instance
   *
   * @param solutionType Solution type
   * @throws ClassNotFoundException
   */
  public OneZeroMax(String solutionType) throws ClassNotFoundException, JMetalException {
    this(solutionType, 512);
  }

  /**
   * Creates a new OneZeroMax problem instance
   *
   * @param solutionType Solution type
   * @param numberOfBits Length of the problem
   */
  public OneZeroMax(String solutionType, Integer numberOfBits) throws JMetalException {
    numberOfVariables = 1;
    numberOfObjectives = 2;
    numberOfConstraints = 0;
    problemName = "OneZeroMax";

    this.solutionType = new BinarySolutionType(this);

    length = new int[numberOfVariables];
    length[0] = numberOfBits;

    if (solutionType.compareTo("Binary") == 0) {
      this.solutionType = new BinarySolutionType(this);
    } else {
      throw new JMetalException("Error: solutiontype type " + solutionType + " invalid");
    }
  }

  /**
   * Evaluates a solutiontype
   *
   * @param solution The solutiontype to evaluate
   */
  public void evaluate(Solution solution) {
    Binary variable;
    int counterOnes;
    int counterZeroes;

    variable = ((Binary) solution.getDecisionVariables()[0]);

    counterOnes = 0;
    counterZeroes = 0;

    for (int i = 0; i < variable.getNumberOfBits(); i++) {
      if (variable.getBits().get(i)) {
        counterOnes++;
      } else {
        counterZeroes++;
      }
    }

    // OneZeroMax is a maximization problem: multiply by -1 to minimize
    solution.setObjective(0, -1.0 * counterOnes);
    solution.setObjective(1, -1.0 * counterZeroes);
  }
}
