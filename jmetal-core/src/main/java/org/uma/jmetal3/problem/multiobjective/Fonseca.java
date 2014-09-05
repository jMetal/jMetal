//  Fonseca.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro, Juan J. Durillo
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

import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.impl.NumericSolutionImpl;
import org.uma.jmetal3.problem.impl.UnconstrainedContinuousProblemImpl;

import java.util.ArrayList;

/** Class representing problem Fonseca */
public class Fonseca extends UnconstrainedContinuousProblemImpl {

  /** Constructor */
  public Fonseca()  {
    setNumberOfVariables(3);
    setNumberOfObjectives(2);
    setName("Fonseca");

    ArrayList<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    ArrayList<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-4.0);
      upperLimit.add(4.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  @Override
	public Solution createSolution() {
		Solution solution = new NumericSolutionImpl<Double>(this) ;

		return solution ; 
	}
	
  /** Evaluate() method */
  @Override
  public void evaluate(Solution solution) {
    int numberOfVariables = getNumberOfVariables() ;

    double[] f = new double[getNumberOfObjectives()];
    double[] x = new double[numberOfVariables] ;
    
    for (int i = 0; i < numberOfVariables; i++) {
    	x[i] = (double)solution.getVariableValue(i) ;
    }
    
    double sum1 = 0.0;
    for (int i = 0; i < numberOfVariables; i++) {
      sum1 += StrictMath.pow(x[i] - (1.0 / StrictMath.sqrt((double) numberOfVariables)), 2.0);
    }
    double exp1 = StrictMath.exp((-1.0) * sum1);
    f[0] = 1 - exp1;

    double sum2 = 0.0;
    for (int i = 0; i < numberOfVariables; i++) {
      sum2 += StrictMath.pow(x[i] + (1.0 / StrictMath.sqrt((double) numberOfVariables)), 2.0);
    }
    double exp2 = StrictMath.exp((-1.0) * sum2);
    f[1] = 1 - exp2;

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);
  }
}
