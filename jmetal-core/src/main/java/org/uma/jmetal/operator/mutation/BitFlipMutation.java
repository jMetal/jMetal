//  BitFlipMutation.java
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
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.BinarySolutionType;
import org.uma.jmetal.encoding.solutiontype.IntSolutionType;
import org.uma.jmetal.encoding.variable.Binary;
import org.uma.jmetal.encoding.variable.BinaryReal;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.HashMap;
import java.util.logging.Level;

/**
 * This class implements a bit flip mutation operator.
 * NOTE: the operator is applied to binary or integer solutions, considering the
 * whole solutiontype as a single encoding.variable.
 */
public class BitFlipMutation extends Mutation {
  private static final long serialVersionUID = -3349165791496573889L;

  private double mutationProbability = 0;

  /**
   * Constructor
   * Creates a new instance of the Bit Flip mutation operator
   * @deprecated
   */
  @Deprecated
  public BitFlipMutation(HashMap<String, Object> parameters) {
    super(parameters);
    if (parameters.get("probability") != null) {
      mutationProbability = (Double) parameters.get("probability");
    }
  }

  /** Constructor */
  private BitFlipMutation(Builder builder) {
    addValidSolutionType(BinarySolutionType.class);
    addValidSolutionType(BinaryRealSolutionType.class);
    addValidSolutionType(IntSolutionType.class);

    mutationProbability = builder.mutationProbability;
  }

  public double getMutationProbability() {
    return mutationProbability;
  }

  /**
   * Perform the mutation operation
   *
   * @param probability Mutation probability
   * @param solution    The solutiontype to mutate
   * @throws org.uma.jmetal.util.JMetalException
   */
  public void doMutation(double probability, Solution solution) throws JMetalException {
    try {
      if ((solution.getType().getClass() == BinarySolutionType.class) ||
        (solution.getType().getClass() == BinaryRealSolutionType.class)) {
        for (int i = 0; i < solution.getDecisionVariables().length; i++) {
          for (int j = 0;
               j < ((Binary) solution.getDecisionVariables()[i]).getNumberOfBits(); j++) {
            if (PseudoRandom.randDouble() < probability) {
              ((Binary) solution.getDecisionVariables()[i]).getBits().flip(j);
            }
          }
        }

        if (solution.getType().getClass() == BinaryRealSolutionType.class) {
          for (int i = 0; i < solution.getDecisionVariables().length; i++) {
            ((BinaryReal) solution.getDecisionVariables()[i]).decode();
          }
        }
      } else {
        for (int i = 0; i < solution.getDecisionVariables().length; i++) {
          if (PseudoRandom.randDouble() < probability) {
            int value = PseudoRandom.randInt(
              (int) solution.getDecisionVariables()[i].getLowerBound(),
              (int) solution.getDecisionVariables()[i].getUpperBound());
            solution.getDecisionVariables()[i].setValue(value);
          }
        }
      }
    } catch (ClassCastException e1) {
      Configuration.logger_.log(Level.SEVERE,
        "BitFlipMutation.doMutation: ClassCastException error" + e1.getMessage(), e1);
      Class<String> cls = java.lang.String.class;
      String name = cls.getName();
      throw new JMetalException("Exception in " + name + ".doMutation()");
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
      throw new JMetalException("BitFlipMutation.execute: the solutiontype " +
        "is not of the right type. The type should be 'Binary', " +
        "'BinaryReal' or 'Int', but " + solution.getType() + " is obtained");
    }

    doMutation(mutationProbability, solution);
    return solution;
  }

  /** Builder class */
  public static class Builder {
    private double mutationProbability = 0.0 ;

    public Builder() {
    }

    public Builder(double probability) {
      mutationProbability = probability ;
    }

    public Builder probability(double probability) {
      mutationProbability = probability ;

      return this ;
    }

    public BitFlipMutation build() {
      return new BitFlipMutation(this) ;
    }
  }
}
