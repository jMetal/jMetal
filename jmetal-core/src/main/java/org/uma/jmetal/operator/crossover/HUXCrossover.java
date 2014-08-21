//  HUXCrossover.java
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
import org.uma.jmetal.encoding.solutiontype.BinaryRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.BinarySolutionType;
import org.uma.jmetal.encoding.variable.Binary;
import org.uma.jmetal.encoding.variable.BinaryReal;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.HashMap;
import java.util.logging.Level;

/**
 * This class allows to apply a HUX crossover operator using two parent
 * solutions.
 * NOTE: the operator is applied to the first encoding.variable of the solutions, and
 * the type of the solutions must be Binary or BinaryReal
 */
public class HUXCrossover extends Crossover {
  private static final long serialVersionUID = -5600218276088232241L;

  private double probability = 1.0;

  @Deprecated
  public HUXCrossover(HashMap<String, Object> parameters) {
    super(parameters);

    addValidSolutionType(BinarySolutionType.class);
    addValidSolutionType(BinaryRealSolutionType.class);

    if (parameters.get("probability") != null) {
      probability = (Double) parameters.get("probability");
    }
  }

  /** Constructor */
  private HUXCrossover(Builder builder) {
    addValidSolutionType(BinarySolutionType.class);
    addValidSolutionType(BinaryRealSolutionType.class);

    probability = builder.crossoverProbability;
  }

  /* Getter */
  public double getCrossoverProbability() {
    return this.probability;
  }

  /**
   * Perform the crossover operation
   *
   * @param probability Crossover probability
   * @param parent1     The first parent
   * @param parent2     The second parent
   * @return An array containing the two offsprings
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
        for (int var = 0; var < parent1.getDecisionVariables().length; var++) {
          Binary p1 = (Binary) parent1.getDecisionVariables()[var];
          Binary p2 = (Binary) parent2.getDecisionVariables()[var];

          for (int bit = 0; bit < p1.getNumberOfBits(); bit++) {
            if ((p1.getBits().get(bit) != p2.getBits().get(bit)) && (PseudoRandom.randDouble() < 0.5)) {
              ((Binary) offSpring[0].getDecisionVariables()[var])
                      .getBits().set(bit, p2.getBits().get(bit));
              ((Binary) offSpring[1].getDecisionVariables()[var])
                      .getBits().set(bit, p1.getBits().get(bit));
            }
          }
        }
        //7. Decode the results
        if (parent1.getType().getClass() == BinaryRealSolutionType.class) {
          for (int i = 0; i < offSpring[0].getDecisionVariables().length; i++) {
            ((BinaryReal) offSpring[0].getDecisionVariables()[i]).decode();
            ((BinaryReal) offSpring[1].getDecisionVariables()[i]).decode();
          }
        }
      }
    } catch (ClassCastException e1) {
      JMetalLogger.logger.log(Level.SEVERE,
              "HUXCrossover.doCrossover: Cannot perfom " + "SinglePointCrossover ",
              e1);
      Class<String> cls = java.lang.String.class;
      String name = cls.getName();
      throw new JMetalException("Exception in " + name + ".doCrossover()");
    }
    return offSpring;
  }

  /** Execute() method */
  public Object execute(Object object) throws JMetalException {
    Solution[] parents = (Solution[]) object;

    if (parents.length < 2) {
      throw new JMetalException("HUXCrossover.execute: operator needs two parents");
    }

    if (!solutionTypeIsValid(parents)) {
      throw new JMetalException("HUXCrossover.execute: the solutions " +
              "are not of the right type. The type should be 'Binary' of " +
              "'BinaryReal', but " +
              parents[0].getType() + " and " +
              parents[1].getType() + " are obtained");
    }

    Solution[] offSpring = doCrossover(probability, parents[0], parents[1]);

    for (Solution solution : offSpring) {
      solution.setCrowdingDistance(0.0);
      solution.setRank(0);
    }

    return offSpring;
  }

  /** Builder class */
  public static class Builder {
    private double crossoverProbability;

    public Builder() {
      crossoverProbability = 1.0 ;
    }

    public Builder probability(double probability) {
      if ((probability < 0) || (probability > 1.0)) {
        throw new JMetalException("Probability value invalid: " + probability) ;
      } else {
        crossoverProbability = probability;
      }

      return this ;
    }

    public HUXCrossover build() {
      return new HUXCrossover(this) ;
    }
  }
}
