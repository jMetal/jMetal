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

package org.uma.jmetal.operator.mutation;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.PermutationSolutionType;
import org.uma.jmetal.encoding.variable.Permutation;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class implements a swap mutation. The solutiontype type of the solutiontype
 * must be Permutation.
 */
public class SwapMutation extends Mutation {
  /**
   *
   */
  private static final long serialVersionUID = -3982393451733347035L;

  /**
   * Valid solutiontype types to apply this operator
   */
  private static final List<Class<PermutationSolutionType>> VALID_TYPES =
    Arrays.asList(PermutationSolutionType.class);

  private Double mutationProbability_ = null;

  /**
   * Constructor
   */
  public SwapMutation(HashMap<String, Object> parameters) {
    super(parameters);

    if (parameters.get("probability") != null) {
      mutationProbability_ = (Double) parameters.get("probability");
    }
  }

  /**
   * Performs the operation
   *
   * @param probability Mutation probability
   * @param solution    The solutiontype to mutate
   * @throws org.uma.jmetal.util.JMetalException
   */
  public void doMutation(double probability, Solution solution) throws JMetalException {
    int permutation[];
    int permutationLength;
    if (solution.getType().getClass() == PermutationSolutionType.class) {

      permutationLength = ((Permutation) solution.getDecisionVariables()[0]).getLength();
      permutation = ((Permutation) solution.getDecisionVariables()[0]).getVector();

      if (PseudoRandom.randDouble() < probability) {
        int pos1;
        int pos2;

        pos1 = PseudoRandom.randInt(0, permutationLength - 1);
        pos2 = PseudoRandom.randInt(0, permutationLength - 1);

        while (pos1 == pos2) {
          if (pos1 == (permutationLength - 1)) {
            pos2 = PseudoRandom.randInt(0, permutationLength - 2);
          } else {
            pos2 = PseudoRandom.randInt(pos1, permutationLength - 1);
          }
        }
        // swap
        int temp = permutation[pos1];
        permutation[pos1] = permutation[pos2];
        permutation[pos2] = temp;
      }
    } else {
      Configuration.logger_.severe("SwapMutation.doMutation: invalid type. " +
        "" + solution.getDecisionVariables()[0].getVariableType());

      Class<String> cls = java.lang.String.class;
      String name = cls.getName();
      throw new JMetalException("Exception in " + name + ".doMutation()");
    }
  }

  /**
   * Executes the operation
   *
   * @param object An object containing the solutiontype to mutate
   * @return an object containing the mutated solutiontype
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Object execute(Object object) throws JMetalException {
    Solution solution = (Solution) object;

    if (!VALID_TYPES.contains(solution.getType().getClass())) {
      Configuration.logger_.severe("SwapMutation.execute: the solutiontype " +
        "is not of the right type. The type should be 'Binary', " +
        "'BinaryReal' or 'Int', but " + solution.getType() + " is obtained");

      Class<String> cls = java.lang.String.class;
      String name = cls.getName();
      throw new JMetalException("Exception in " + name + ".execute()");
    }


    this.doMutation(mutationProbability_, solution);
    return solution;
  }
}
