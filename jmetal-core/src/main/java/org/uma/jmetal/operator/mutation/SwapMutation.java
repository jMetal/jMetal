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
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.HashMap;

/** This class implements a swap mutation over a permutation encoded solution */
public class SwapMutation extends Mutation {
  private static final long serialVersionUID = -3982393451733347035L;

  private double mutationProbability = 0.0 ;

  @Deprecated
  public SwapMutation(HashMap<String, Object> parameters) {
    super(parameters);

    if (parameters.get("probability") != null) {
      mutationProbability = (double) parameters.get("probability");
    }

    addValidSolutionType(PermutationSolutionType.class);
  }

  /** Constructor */
  private SwapMutation(Builder builder) {
    addValidSolutionType(PermutationSolutionType.class);

    mutationProbability = builder.mutationProbability ;
  }

  public double getMutationProbability() {
    return mutationProbability;
  }

  /** Perform mutation */
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
      /* Other encodings here in the future */
    }
  }

  /** execute() method */
  public Object execute(Object object) throws JMetalException {
    if (null == object) {
      throw new JMetalException("Null parameter") ;
    } else if (!(object instanceof Solution)) {
      throw new JMetalException("Invalid parameter class") ;
    }

    Solution solution = (Solution) object;

    if (!solutionTypeIsValid(solution)) {
      throw new JMetalException("SwapMutation.execute: the solution " +
              "type " + solution.getType() + " is not allowed with this operator");
    }

    this.doMutation(mutationProbability, solution);
    return solution;
  }

  /** Builder class */
  public static class Builder {
    private double mutationProbability ;

    public Builder() {
    }

    public Builder probability(double probability) {
      mutationProbability = probability ;

      return this ;
    }

    public SwapMutation build() {
      return new SwapMutation(this) ;
    }
  }
}
