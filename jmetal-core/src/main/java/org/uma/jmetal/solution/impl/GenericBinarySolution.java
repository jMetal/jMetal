//  GenericBinarySolution.java
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

package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

/**
 * Defines an implementation of a binary solution
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class GenericBinarySolution extends AbstractGenericSolution<BitSet, BinaryProblem> implements BinarySolution {
  /** Constructor */
  public GenericBinarySolution(BinaryProblem problem) {
    this.problem = problem ;

    objectives = new ArrayList<>(problem.getNumberOfObjectives()) ;
    variables = new ArrayList<>(problem.getNumberOfVariables()) ;
    overallConstraintViolationDegree = 0.0 ;

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      variables.add(createNewBitSet(problem.getNumberOfBits(i)));
    }

    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      objectives.add(new Double(0.0)) ;
    }
  }

  /** Copy constructor */
  public GenericBinarySolution(GenericBinarySolution solution) {
    problem = solution.problem ;
    objectives = new ArrayList<>() ;
    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      objectives.add(solution.getObjective(i)) ;
    }

    variables = new ArrayList<>() ;
    for (BitSet var : solution.variables) {
      variables.add((BitSet) var.clone()) ;
    }

    overallConstraintViolationDegree = solution.overallConstraintViolationDegree ;
    attributes = new HashMap(solution.attributes) ;
  }

  private BitSet createNewBitSet(int numberOfBits) {
    BitSet bitSet = new BitSet(numberOfBits) ;

    for (int i = 0; i < numberOfBits; i++) {
      if (randomGenerator.nextDouble() < 0.5) {
        bitSet.set(i, true);
      } else {
        bitSet.set(i, false);
      }
    }
    return bitSet ;
  }

  @Override
  public int getNumberOfBits(int index) {
    return variables.get(index).length() ;
  }

  @Override
  public Solution copy() {
    return new GenericBinarySolution(this);
  }

  @Override
  public int getTotalNumberOfBits() {
    int sum = 0 ;
    for (BitSet binaryString : variables) {
      sum += binaryString.length() ;
    }

    return sum ;
  }

  @Override
  public String getVariableValueString(int index) {
    String result = "" ;
    for (BitSet var : variables) {
      for (int i = 0; i < var.length() ; i++) {
        if (var.get(i)) {
          result += "1" ;
        }
        else {
          result+= "0" ;
        }
      }
      result += "\t" ;
    }
    return result ;
  }
}
