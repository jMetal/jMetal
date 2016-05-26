//  CEC2009_UF2.java
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

package org.uma.jmetal.problem.multiobjective.UF;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem CEC2009_UF2
 */
@SuppressWarnings("serial")
public class UF2 extends AbstractDoubleProblem {

  /**
   * Constructor.
   * Creates a default instance of problem CEC2009_UF2 (30 decision variables)
   */
  public UF2()  {
    this(30);
  }

  /**
   * Creates a new instance of problem CEC2009_UF2.
   * @param numberOfVariables Number of variables.
   */
  public UF2(int numberOfVariables) {
    setNumberOfVariables(numberOfVariables) ;
    setNumberOfObjectives(2) ;
    setNumberOfConstraints(0) ;
    setName("UF") ;

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    lowerLimit.add(0.0);
    upperLimit.add(1.0);
    for (int i = 1; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-1.0);
      upperLimit.add(1.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
    double[] x = new double[getNumberOfVariables()];
    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      x[i] = solution.getVariableValue(i) ;
    }

    int count1, count2;
    double sum1, sum2, yj;
    sum1   = sum2   = 0.0;
    count1 = count2 = 0;

    for (int j = 2 ; j <= getNumberOfVariables(); j++) {
      if(j % 2 == 0) {
        yj = x[j-1] -
            (0.3 * x[0] * x[0] * Math.cos(24 * Math.PI * x[0] + 4 * j * Math.PI / getNumberOfVariables()) + 0.6 * x[0])*
                Math.sin(6.0 * Math.PI* x[0] + j * Math.PI / getNumberOfVariables());
        sum2 += yj*yj;
        count2++;
      } else {

        yj = x[j-1] -
            (0.3 * x[0] * x[0] * Math.cos(24 * Math.PI * x[0] + 4 * j * Math.PI / getNumberOfVariables()) + 0.6 * x[0])*
                Math.cos(6.0 * Math.PI* x[0] + j * Math.PI / getNumberOfVariables());

        sum1 += yj*yj;
        count1++;
      }
    }

    solution.setObjective(0, x[0] + 2.0 * sum1 / (double)count1);
    solution.setObjective(1, 1.0 - Math.sqrt(x[0]) + 2.0 * sum2 / (double)count2);
  }
}
