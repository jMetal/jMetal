//  PMXCrossover.java
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

package org.uma.jmetal.operator.crossover;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.solutiontype.PermutationSolutionType;
import org.uma.jmetal.encoding.variable.Permutation;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;

/**
 * This class allows to apply a PMX crossover operator using two parent
 * solutions.
 * NOTE: the operator is applied to the first encoding.variable of the solutions, and
 * the type of those variables must be VariableType_.Permutation.
 */
public class PMXCrossover extends Crossover {
  private static final long serialVersionUID = -4059314233890056350L;
  private static final double DEFAULT_PROBABILITY = 0.9 ;

  private double crossoverProbability ;

  /** Constructor */
  private PMXCrossover(Builder builder) {
    addValidSolutionType(PermutationSolutionType.class);

    crossoverProbability = builder.crossoverProbability ;
  }

  /* Getter */
  public double getCrossoverProbability() {
    return crossoverProbability ;
  }

  /** Builder class */
  public static class Builder {
    private double crossoverProbability ;

    public Builder() {
      crossoverProbability = DEFAULT_PROBABILITY ;
    }

    public Builder setProbability(double probability) {
      if ((probability < 0) || (probability > 1.0)) {
        throw new JMetalException("Probability value invalid: " + probability) ;
      } else {
        crossoverProbability = probability;
      }

      return this ;
    }

    public PMXCrossover build() {
      return new PMXCrossover(this) ;
    }
  }

  /** Execute() method */
  public Object execute(Object object) throws JMetalException {
    if (null == object) {
      throw new JMetalException("Null parameter") ;
    } else if (!(object instanceof Solution[])) {
      throw new JMetalException("Invalid parameter class") ;
    }

    Solution[] parents = (Solution[]) object;

    if (parents.length != 2) {
      throw new JMetalException("PMXCrossover.execute: operator needs two " +
              "parents");
    }

    if (!solutionTypeIsValid(parents)) {
      throw new JMetalException("PMXCrossover.execute: the solutions " +
              "type " + parents[0].getType() + " is not allowed with this operator");
    }

    Solution[] offspring = doCrossover(crossoverProbability, parents[0], parents[1]);

    return offspring;
  }

  /**
   * Perform the crossover operation
   *
   * @param probability Crossover setProbability
   * @param parent1     The first parent
   * @param parent2     The second parent
   * @return An array containig the two offsprings
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Solution[] doCrossover(double probability, Solution parent1, Solution parent2) {

    Solution[] offspring = new Solution[2];

    offspring[0] = new Solution(parent1);
    offspring[1] = new Solution(parent2);

    int permutationLength;

    permutationLength = ((Permutation) parent1.getDecisionVariables()[0]).getLength();

    int parent1Vector[] = ((Permutation) parent1.getDecisionVariables()[0]).getVector();
    int parent2Vector[] = ((Permutation) parent2.getDecisionVariables()[0]).getVector();
    int offspring1Vector[] = ((Permutation) offspring[0].getDecisionVariables()[0]).getVector();
    int offspring2Vector[] = ((Permutation) offspring[1].getDecisionVariables()[0]).getVector();

    if (PseudoRandom.randDouble() < probability) {
      int cuttingPoint1;
      int cuttingPoint2;

      // STEP 1: Get two cutting points
      cuttingPoint1 = PseudoRandom.randInt(0, permutationLength - 1);
      cuttingPoint2 = PseudoRandom.randInt(0, permutationLength - 1);
      while (cuttingPoint2 == cuttingPoint1) {
        cuttingPoint2 = PseudoRandom.randInt(0, permutationLength - 1);
      }

      if (cuttingPoint1 > cuttingPoint2) {
        int swap;
        swap = cuttingPoint1;
        cuttingPoint1 = cuttingPoint2;
        cuttingPoint2 = swap;
      }
      // STEP 2: Get the subchains to interchange
      int replacement1[] = new int[permutationLength];
      int replacement2[] = new int[permutationLength];
      for (int i = 0; i < permutationLength; i++) {
        replacement1[i] = replacement2[i] = -1;
      }

      //      STEP 3: Interchange
      for (int i = cuttingPoint1; i <= cuttingPoint2; i++) {
        offspring1Vector[i] = parent2Vector[i];
        offspring2Vector[i] = parent1Vector[i];

        replacement1[parent2Vector[i]] = parent1Vector[i];
        replacement2[parent1Vector[i]] = parent2Vector[i];
      }

      //      STEP 4: Repair offsprings
      for (int i = 0; i < permutationLength; i++) {
        if ((i >= cuttingPoint1) && (i <= cuttingPoint2)) {
          continue;
        }

        int n1 = parent1Vector[i];
        int m1 = replacement1[n1];

        int n2 = parent2Vector[i];
        int m2 = replacement2[n2];

        while (m1 != -1) {
          n1 = m1;
          m1 = replacement1[m1];
        } // while
        while (m2 != -1) {
          n2 = m2;
          m2 = replacement2[m2];
        } // while
        offspring1Vector[i] = n1;
        offspring2Vector[i] = n2;
      }
    }

    return offspring;
  }
}
