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

package org.uma.jmetal.problem.multiobjective.lz09;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem LZ09F8
 */
@SuppressWarnings("serial")
public class LZ09F8 extends AbstractDoubleProblem {

  private LZ09 lz09;

  /**
   * Creates a default LZ09F8 problem (10 variables and 2 objectives)
   */
  public LZ09F8() {
    this(21, 4, 21);
  }

  /**
   * Creates a LZ09F8 problem instance
   */
  public LZ09F8(Integer ptype,
                Integer dtype,
                Integer ltype) throws JMetalException {
    setNumberOfVariables(10);
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("LZ09F8");

    lz09 = new LZ09(getNumberOfVariables(),
            getNumberOfObjectives(),
            ptype,
            dtype,
            ltype);

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  /** Evaluate() method */
  public void evaluate(DoubleSolution solution) {
    List<Double> x = new ArrayList<Double>(getNumberOfVariables());
    List<Double> y = new ArrayList<Double>(getNumberOfObjectives());

    for (int i = 0; i < getNumberOfVariables(); i++) {
      x.add(solution.getVariableValue(i));
      y.add(0.0);
    }

    lz09.objective(x, y);

    for (int i = 0; i < getNumberOfObjectives(); i++) {
      solution.setObjective(i, y.get(i));
    }
  }
}

