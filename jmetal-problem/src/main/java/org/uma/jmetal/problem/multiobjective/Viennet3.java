//  Viennet3.java
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

package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem Viennet3
 */
public class Viennet3 extends AbstractDoubleProblem {
  
 /** 
  * Constructor.
  * Creates a default instance of the Viennet3 problem.
  */
  public Viennet3() {
    setNumberOfVariables(2);
    setNumberOfObjectives(3);
    setNumberOfConstraints(0);
    setName("Viennet3") ;

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-3.0);
      upperLimit.add(3.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
    int numberOfVariables = getNumberOfVariables() ;

    double[] f = new double[getNumberOfObjectives()];
    double[] x = new double[numberOfVariables] ;

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = solution.getVariableValue(i) ;
    }
                 
    f[0] = 0.5 * (x[0]*x[0] + x[1]*x[1]) + Math.sin(x[0]*x[0] + x[1]*x[1]) ;

    // Second function
    double value1 = 3.0 * x[0] - 2.0 * x[1] + 4.0 ;
    double value2 = x[0] - x[1] + 1.0 ;
    f[1] = (value1 * value1)/8.0 + (value2 * value2)/27.0 + 15.0 ;

    // Third function
    f[2] = 1.0 / (x[0]*x[0] + x[1]*x[1]+1) - 1.1 *
          Math.exp(-(x[0]*x[0])-(x[1]*x[1])) ;

        
    for (int i = 0; i < getNumberOfObjectives(); i++)
      solution.setObjective(i,f[i]);        
  }
}


