//  OneZeroMax.java
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
//  along with this program.  If not, see <http://www.gnu.org/licenses/>. * OneZeroMax.java

package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.JMetalException;

import java.util.BitSet;

/**
 * Class representing problem OneZeroMax. The problem consist of maximizing the
 * number of '1's and '0's in a binary string.
 */
@SuppressWarnings("serial")
public class OneZeroMax extends AbstractBinaryProblem {
  private int bits ;
  /** Constructor */
  public OneZeroMax() throws JMetalException {
    this(512);
  }

  /** Constructor */
  public OneZeroMax(Integer numberOfBits) throws JMetalException {
    setNumberOfVariables(1);
    setNumberOfObjectives(2);
    setName("OneZeroMax");

    bits = numberOfBits ;
  }

  @Override
  protected int getBitsPerVariable(int index) {
  	if (index != 0) {
  		throw new JMetalException("Problem OneZeroMax has only a variable. Index = " + index) ;
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
    int counterZeroes;

    counterOnes = 0;
    counterZeroes = 0;

    BitSet bitset = solution.getVariableValue(0) ;

    for (int i = 0; i < bitset.length(); i++) {
      if (bitset.get(i)) {
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
