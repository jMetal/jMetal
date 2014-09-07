//  BitFlipMutation.java
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

package org.uma.jmetal3.operator.mutation.impl;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal3.encoding.BinarySolution;
import org.uma.jmetal3.operator.mutation.MutationOperator;

/**
 * This class implements a bit flip mutation operator.
 */
public class BitFlipMutation implements MutationOperator<BinarySolution> {
  private static final long serialVersionUID = -3349165791496573889L;

  private double mutationProbability = 0;

  /** Constructor */
  private BitFlipMutation(double mutationProbability) {
    this.mutationProbability = mutationProbability;
  }

  /** Constructor */
  private BitFlipMutation(Builder builder) {
    mutationProbability = builder.mutationProbability;
  }

  /* Getter */
  public double getMutationProbability() {
    return mutationProbability;
  }

  /** Builder class */
  public static class Builder {
    private double mutationProbability = 0.0 ;

    public Builder() {
    }

    public Builder(double probability) {
      mutationProbability = probability ;
    }

    public Builder setProbability(double probability) {
      mutationProbability = probability ;

      return this ;
    }

    public BitFlipMutation build() {
      return new BitFlipMutation(this) ;
    }
  }

  /** Execute() method */
  @Override
  public BinarySolution execute(BinarySolution solution) {
    if (null == solution) {
      throw new JMetalException("Null parameter") ;
    }

    doMutation(mutationProbability, solution);
    return solution;  }

  /**
   * Perform the mutation operation
   *
   * @param probability Mutation setProbability
   * @param solution    The solution to mutate
   */
  public void doMutation(double probability, BinarySolution solution)  {
    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      for (int j = 0; j < solution.getVariableValue(i).length(); j++) {
        if (PseudoRandom.randDouble() <= probability) {
          solution.getVariableValue(i).flip(j);
        }
      }
    }
  }
}
