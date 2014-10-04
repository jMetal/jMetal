//  NMMin.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2014 Antonio J. Nebro
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
package org.uma.jmetal3.problem.multiobjective;

import org.uma.jmetal3.encoding.IntegerSolution;
import org.uma.jmetal3.encoding.impl.IntegerSolutionImpl;
import org.uma.jmetal3.problem.impl.IntegerProblemImpl;

import java.util.ArrayList;

/**
 * Created by Antonio J. Nebro on 03/07/14.
 * Bi-objective problem for testing integer encoding.
 * Objective 1: minimizing the distance to value N
 * Objective 2: minimizing the distance to value M
 */
public class NMMin extends IntegerProblemImpl {
  private int valueN ;
  private int valueM ;

  public NMMin() {
    this(10, 100, -100, -1000, +1000);
  }

  /** Constructor */
  public NMMin(int numberOfVariables, int n, int m, int lowerBound, int upperBound)  {
    valueN = n ;
    valueM = m ;
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(2);
    setName("NMMin");

    ArrayList<Integer> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    ArrayList<Integer> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(lowerBound);
      upperLimit.add(upperBound);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  @Override
  public IntegerSolution createSolution() {
    IntegerSolution solution = new IntegerSolutionImpl(this) ;

    return solution ;
  }

  /** Evaluate() method */
  @Override
  public void evaluate(IntegerSolution solution) {
    int approximationToN;
    int approximationToM ;

    approximationToN = 0;
    approximationToM = 0;

    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      int value = solution.getVariableValue(i) ;
      approximationToN += Math.abs(valueN - value) ;
      approximationToM += Math.abs(valueM - value) ;
    }

    solution.setObjective(0, approximationToN);
    solution.setObjective(1, approximationToM);
  }
}
