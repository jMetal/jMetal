//  Schaffer.java
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

package org.uma.jmetal3.problem.multiobjective;

import org.uma.jmetal3.encoding.DoubleSolution;
import org.uma.jmetal3.encoding.impl.DoubleSolutionImpl;
import org.uma.jmetal3.problem.impl.ContinuousProblemImpl;

import java.util.ArrayList;

/**
 * Class representing problem Schaffer
 */
public class Schaffer extends ContinuousProblemImpl {
  private static final long serialVersionUID = -2366503015218789989L;

  /**
   * Constructor.
   * Creates a default instance of problem Schaffer
   */
  public Schaffer() {
    setNumberOfVariables(1);
    setNumberOfObjectives(2);
    setName("Schaffer");

    ArrayList<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    ArrayList<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-100000.0);
      upperLimit.add(100000.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  @Override
  public DoubleSolution createSolution() {
    DoubleSolution solution = new DoubleSolutionImpl(this) ;

    return solution ;
  }

  /** Evaluate() method */
  public void evaluate(DoubleSolution solution) {
    double[] f = new double[getNumberOfObjectives()];
    double value = solution.getVariableValue(0) ;

    f[0] = value * value;
    f[1] = (value - 2.0) * (value - 2.0);

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }
}
