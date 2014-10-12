//  SinglePointCrossover.java
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

package org.uma.jmetal.operator.crossover.impl;

import org.uma.jmetal45.util.JMetalException;
import org.uma.jmetal45.util.random.PseudoRandom;
import org.uma.jmetal.encoding.BinarySolution;
import org.uma.jmetal.operator.crossover.CrossoverOperator;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/** Class implementing a single point crossover */
public class SinglePointCrossover implements CrossoverOperator<List<BinarySolution>, List<BinarySolution>> {

  private double crossoverProbability ;

  /** Constructor */
  private SinglePointCrossover() {
    this.crossoverProbability = 0.9;
  }

  /** Constructor */
  private SinglePointCrossover(double crossoverProbability) {
    this.crossoverProbability = crossoverProbability;
  }

  /** Constructor */
  private SinglePointCrossover(Builder builder) {
    crossoverProbability = builder.crossoverProbability;
  }

  /* Getter */
  public double getCrossoverProbability() {
    return crossoverProbability;
  }

  @Override
  public List<BinarySolution> execute(List<BinarySolution> solutions) {
    if (solutions == null) {
      throw new JMetalException("Null parameter") ;
    } else if (solutions.size() != 2) {
      throw new JMetalException("There must be two parents instead of " + solutions.size()) ;
    }

    return doCrossover(crossoverProbability, solutions.get(0), solutions.get(1)) ;
  }

  /** Builder class */
  public static class Builder {
    private double crossoverProbability;

    public Builder() {
      crossoverProbability = 0.9 ;
    }

    public Builder setProbability(double probability) {
      if ((probability < 0) || (probability > 1.0)) {
        throw new JMetalException("Probability value invalid: " + probability) ;
      } else {
        crossoverProbability = probability;
      }

      return this ;
    }

    public SinglePointCrossover build() {
      return new SinglePointCrossover(this) ;
    }
  }

  /**
   * Perform the crossover operation.
   *
   * @param probability Crossover setProbability
   * @param parent1     The first parent
   * @param parent2     The second parent
   * @return An array containing the two offspring
   * @throws org.uma.jmetal45.util.JMetalException
   */
  public List<BinarySolution> doCrossover(double probability, BinarySolution parent1, BinarySolution parent2)  {
    List<BinarySolution> offspring = new ArrayList<>(2);
    offspring.add((BinarySolution) parent1.copy()) ;
    offspring.add((BinarySolution) parent2.copy()) ;

    if (PseudoRandom.randDouble() < probability) {
      // 1. Get the total number of bits
      int totalNumberOfBits = parent1.getTotalNumberOfBits();

      // 2. Calculate the point to make the crossover
      int crossoverPoint = PseudoRandom.randInt(0, totalNumberOfBits - 1);

      // 3. Compute the variable containing the crossover bit
      int variable = 0;
      int bitsAccount = parent1.getVariableValue(variable).length() ;
      while (bitsAccount < (crossoverPoint + 1)) {
        variable++;
        bitsAccount += parent1.getVariableValue(variable).length() ;
      }

      //4. Compute the bit into the selected encoding.variable
      int diff = bitsAccount - crossoverPoint;
      int intoVariableCrossoverPoint = parent1.getVariableValue(variable).length() - diff ;

      //5. Make the crossover into the variable;
      BitSet offspring1, offspring2;
      offspring1 = (BitSet) parent1.getVariableValue(variable).clone();
      offspring2 = (BitSet) parent1.getVariableValue(variable).clone();

      for (int i = intoVariableCrossoverPoint; i < offspring1.length(); i++) {
        boolean swap = offspring1.get(i);
        offspring1.set(i, offspring2.get(i));
        offspring2.set(i, swap);
      }

      offspring.get(0).setVariableValue(variable, offspring1);
      offspring.get(1).setVariableValue(variable, offspring2);

      //6. Apply the crossover to the other variables
      for (int i = 0; i < variable; i++) {
        offspring.get(0).setVariableValue(i, parent2.getVariableValue(i));
        offspring.get(1).setVariableValue(i, parent1.getVariableValue(i));
      }

    }
    return offspring ;
  }
}
