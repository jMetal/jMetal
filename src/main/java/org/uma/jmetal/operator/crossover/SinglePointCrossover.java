//  SinglePointCrossover.java
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
import org.uma.jmetal.core.SolutionType;
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.BinarySolutionType;
import org.uma.jmetal.encoding.solutiontype.IntSolutionType;
import org.uma.jmetal.encoding.variable.Binary;
import org.uma.jmetal.encoding.variable.BinaryReal;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * This class allows to apply a Single Point crossover operator using two parent
 * solutions.
 */
public class SinglePointCrossover extends Crossover {
  /**
   *
   */
  private static final long serialVersionUID = 2375915160877386980L;

  /**
   * Valid solutiontype types to apply this operator
   */
  private static final List<Class<? extends SolutionType>> VALID_TYPES =
      Arrays.asList(BinarySolutionType.class,
          BinaryRealSolutionType.class,
          IntSolutionType.class);

  private Double crossoverProbability_ = null;

  /**
   * Constructor
   * Creates a new instance of the single point crossover operator
   */
  public SinglePointCrossover(HashMap<String, Object> parameters) {
    super(parameters);
    if (parameters.get("probability") != null) {
      crossoverProbability_ = (Double) parameters.get("probability");
    }
  }

  /**
   * Perform the crossover operation.
   *
   * @param probability Crossover probability
   * @param parent1     The first parent
   * @param parent2     The second parent
   * @return An array containig the two offsprings
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Solution[] doCrossover(double probability,
      Solution parent1,
      Solution parent2) throws JMetalException {
    Solution[] offSpring = new Solution[2];
    offSpring[0] = new Solution(parent1);
    offSpring[1] = new Solution(parent2);
    try {
      if (PseudoRandom.randDouble() < probability) {
        if ((parent1.getType().getClass() == BinarySolutionType.class) ||
            (parent1.getType().getClass() == BinaryRealSolutionType.class)) {
          // 1. Compute the total number of bits
          int totalNumberOfBits = 0;
          for (int i = 0; i < parent1.getDecisionVariables().length; i++) {
            totalNumberOfBits +=
                ((Binary) parent1.getDecisionVariables()[i]).getNumberOfBits();
          }

          // 2. Calculate the point to make the crossover
          int crossoverPoint = PseudoRandom.randInt(0, totalNumberOfBits - 1);

          // 3. Compute the encoding.variable containing the crossoverPoint bit
          int variable = 0;
          int accountBits =
              ((Binary) parent1.getDecisionVariables()[variable]).getNumberOfBits();

          while (accountBits < (crossoverPoint + 1)) {
            variable++;
            accountBits +=
                ((Binary) parent1.getDecisionVariables()[variable]).getNumberOfBits();
          }

          //4. Compute the bit into the selected encoding.variable
          int diff = accountBits - crossoverPoint;
          int intoVariableCrossoverPoint =
              ((Binary) parent1.getDecisionVariables()[variable]).getNumberOfBits() - diff;

          //5. Make the crossover into the gene;
          Binary offSpring1, offSpring2;
          offSpring1 =
              (Binary) parent1.getDecisionVariables()[variable].deepCopy();
          offSpring2 =
              (Binary) parent2.getDecisionVariables()[variable].deepCopy();

          for (int i = intoVariableCrossoverPoint;
              i < offSpring1.getNumberOfBits();
              i++) {
            boolean swap = offSpring1.getBits().get(i);
            offSpring1.getBits().set(i, offSpring2.getBits().get(i));
            offSpring2.getBits().set(i, swap);
          }

          offSpring[0].getDecisionVariables()[variable] = offSpring1;
          offSpring[1].getDecisionVariables()[variable] = offSpring2;

          //6. Apply the crossover to the other variables
          for (int i = 0; i < variable; i++) {
            offSpring[0].getDecisionVariables()[i] =
                parent2.getDecisionVariables()[i].deepCopy();

            offSpring[1].getDecisionVariables()[i] =
                parent1.getDecisionVariables()[i].deepCopy();

          }

          //7. Decode the results
          if (parent1.getType().getClass() == BinaryRealSolutionType.class) {
            for (int i = 0; i < offSpring[0].getDecisionVariables().length; i++) {
              ((BinaryReal) offSpring[0].getDecisionVariables()[i]).decode();
              ((BinaryReal) offSpring[1].getDecisionVariables()[i]).decode();
            }
          }
        } else {
          int crossoverPoint = PseudoRandom.randInt(0, parent1.numberOfVariables() - 1);
          int valueX1;
          int valueX2;
          for (int i = crossoverPoint; i < parent1.numberOfVariables(); i++) {
            valueX1 = (int) parent1.getDecisionVariables()[i].getValue();
            valueX2 = (int) parent2.getDecisionVariables()[i].getValue();
            offSpring[0].getDecisionVariables()[i].setValue(valueX2);
            offSpring[1].getDecisionVariables()[i].setValue(valueX1);
          }
        }
      }
    } catch (ClassCastException e1) {
      Configuration.logger_.log(Level.SEVERE,
          "SinglePointCrossover.doCrossover: Cannot perfom " + "SinglePointCrossover",
          e1);
      Class<String> cls = java.lang.String.class;
      String name = cls.getName();
      throw new JMetalException("Exception in " + name + ".doCrossover()");
    }
    return offSpring;
  }

  /**
   * Executes the operation
   *
   * @param object An object containing an array of two solutions
   * @return An object containing an array with the offSprings
   * @throws org.uma.jmetal.util.JMetalException
   */
  public Object execute(Object object) throws JMetalException {
    Solution[] parents = (Solution[]) object;

    if (!(VALID_TYPES.contains(parents[0].getType().getClass()) &&
        VALID_TYPES.contains(parents[1].getType().getClass()))) {

      Configuration.logger_.log(Level.SEVERE,
          "SinglePointCrossover.execute: the solutions " +
              "are not of the right type. The type should be 'Binary' or 'Int', but " +
              parents[0].getType() + " and " +
              parents[1].getType() + " are obtained"
          );

      Class<String> cls = java.lang.String.class;
      String name = cls.getName();
      throw new JMetalException("Exception in " + name + ".execute()");
    }

    if (parents.length < 2) {
      Configuration.logger_.log(Level.SEVERE,
          "SinglePointCrossover.execute: operator " +
              "needs two parents"
          );
      Class<String> cls = java.lang.String.class;
      String name = cls.getName();
      throw new JMetalException("Exception in " + name + ".execute()");
    }

    Solution[] offSpring;
    offSpring = doCrossover(crossoverProbability_,
        parents[0],
        parents[1]);

    // Update the offSpring solutions
    for (Solution anOffSpring : offSpring) {
      anOffSpring.setCrowdingDistance(0.0);
      anOffSpring.setRank(0);
    }
    return offSpring;
  }
}
