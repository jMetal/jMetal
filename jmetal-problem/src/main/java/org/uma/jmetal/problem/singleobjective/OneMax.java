//  OneMax.java
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
//  along with this program.  If not, see <http://www.gnu.org/licenses/>. * OneMax.java

package org.uma.jmetal.problem.singleobjective;

import org.uma.jmetal.problem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.JMetalException;

import java.util.BitSet;

/**
 * Class representing problem OneMax. The problem consist of maximizing the
 * number of '1's in a binary string.
 */
@SuppressWarnings("serial")
public class OneMax extends AbstractBinaryProblem {
	private int bits ;
	
  /** Constructor */
  public OneMax() {
    this(256);
  }

  /** Constructor */
  public OneMax(Integer numberOfBits) {
    setNumberOfVariables(1);
    setNumberOfObjectives(1);
    setName("OneMax");

    bits = numberOfBits ;
  }

  @Override
  protected int getBitsPerVariable(int index) {
  	if (index != 0) {
  		throw new JMetalException("Problem OneMax has only a variable. Index = " + index) ;
  	}
  	return bits ;
  }
   
  @Override
  public BinarySolution createSolution() {
    return new DefaultBinarySolution(this) ;
  }

  /** Evaluate() method */
  @Override
  public void evaluate(BinarySolution solution) {
    int counterOnes;

    counterOnes = 0;

    BitSet bitset = solution.getVariableValue(0) ;

    for (int i = 0; i < bitset.length(); i++) {
      if (bitset.get(i)) {
        counterOnes++;
      }
    }

    // OneMax is a maximization problem: multiply by -1 to minimize
    solution.setObjective(0, -1.0 * counterOnes);
  }
}


