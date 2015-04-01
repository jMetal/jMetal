//  SwapMutation.java
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

package org.uma.jmetal.operator.impl.mutation;

import jmetal.core.Solution;
import jmetal.encodings.solutionType.PermutationSolutionType;
import jmetal.encodings.variable.Permutation;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class implements a swap mutation. The solution type of the solution
 * must be Permutation.
 */
public class PermutationSwapMutation implements MutationOperator<PermutationSolution> {
  private double mutationProbability ;
  private JMetalRandom randomGenerator ;

  /**
   * Constructor
   */
  public PermutationSwapMutation(double mutationProbability) {
    if (mutationProbability < 0) {
      throw new JMetalException("Mutation probability is negative: " + mutationProbability) ;
    }
    this.mutationProbability = mutationProbability;
    randomGenerator = JMetalRandom.getInstance() ;
  }

  /** Execute() method */
  @Override
  public PermutationSolution execute(PermutationSolution solution) {
    if (null == solution) {
      throw new JMetalException("Null parameter") ;
    }

    doMutation(mutationProbability, solution);
    return solution;
  }

  /**
   * Performs the operation
   */
  public void doMutation(double probability, PermutationSolution solution) {
    int permutation[] ;
    int permutationLength ;
    permutationLength = solution.getVariableValue(0). ;
    permutationLength = ((Permutation)solution.getDecisionVariables()[0]).getLength() ;
    permutation = ((Permutation)solution.getDecisionVariables()[0]).vector_ ;

    if (PseudoRandom.randDouble() < probability) {
      int pos1 ;
      int pos2 ;

      pos1 = PseudoRandom.randInt(0,permutationLength-1) ;
      pos2 = PseudoRandom.randInt(0,permutationLength-1) ;

      while (pos1 == pos2) {
        if (pos1 == (permutationLength - 1))
          pos2 = PseudoRandom.randInt(0, permutationLength- 2);
        else
          pos2 = PseudoRandom.randInt(pos1, permutationLength- 1);
      } // while
      // swap
      int temp = permutation[pos1];
      permutation[pos1] = permutation[pos2];
      permutation[pos2] = temp;
    } // if
  }
}
