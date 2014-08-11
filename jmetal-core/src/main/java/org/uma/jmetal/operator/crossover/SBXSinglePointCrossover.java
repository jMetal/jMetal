//  SBXSinglePointCrossover.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
// 
//  Copyright (c) 2011 Antonio J. Nebro
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
import org.uma.jmetal.encoding.solutiontype.ArrayRealAndBinarySolutionType;
import org.uma.jmetal.encoding.variable.Binary;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SBXSinglePointCrossover extends Crossover {
  private static final long serialVersionUID = 5802652792685626443L;

  /**
   * EPS defines the minimum difference allowed between real values
   */
  private static final double EPS = 1.0e-14;

  private static final double ETA_C_DEFAULT = 20.0;
  private double distributionIndex_ = ETA_C_DEFAULT;
  /**
   * Valid solution types to apply this operator
   */
  private static final List<Class<ArrayRealAndBinarySolutionType>> VALID_TYPES =
    Arrays.asList(ArrayRealAndBinarySolutionType.class);
  private Double realCrossoverProbability = null;
  private Double binaryCrossoverProbability = null;

  /**
   * Constructor
   */
  public SBXSinglePointCrossover(HashMap<String, Object> parameters) {
    super(parameters);

    if (parameters.get("realCrossoverProbability") != null) {
      realCrossoverProbability = (Double) parameters.get("realCrossoverProbability");
    }
    if (parameters.get("binaryrossoverProbability") != null) {
      binaryCrossoverProbability = (Double) parameters.get("binaryrossoverProbability");
    }
    if (parameters.get("distributionIndex") != null) {
      distributionIndex_ = (Double) parameters.get("distributionIndex");
    }
  }

  /**
   * Perform the crossover operation.
   *
   * @param realProbability Crossover probability
   * @param parent1         The first parent
   * @param parent2         The second parent
   * @return An array containing the two offsprings
   */
  public Solution[] doCrossover(Double realProbability,
    Double binaryProbability,
    Solution parent1,
    Solution parent2) throws JMetalException {

    Solution[] offSpring = new Solution[2];

    offSpring[0] = new Solution(parent1);
    offSpring[1] = new Solution(parent2);

    // SBX crossover
    double rand;
    double y1, y2, yL, yu;
    double c1, c2;
    double alpha, beta, betaq;
    double valueX1, valueX2;
    XReal x1 = new XReal(parent1);
    XReal x2 = new XReal(parent2);
    XReal offs1 = new XReal(offSpring[0]);
    XReal offs2 = new XReal(offSpring[1]);

    int numberOfVariables = x1.size();

    if (PseudoRandom.randDouble() <= realProbability) {
      for (int i = 0; i < x1.size(); i++) {
        valueX1 = x1.getValue(i);
        valueX2 = x2.getValue(i);
        if (PseudoRandom.randDouble() <= 0.5) {
          if (java.lang.Math.abs(valueX1 - valueX2) > EPS) {

            if (valueX1 < valueX2) {
              y1 = valueX1;
              y2 = valueX2;
            } else {
              y1 = valueX2;
              y2 = valueX1;
            }

            yL = x1.getLowerBound(i);
            yu = x1.getUpperBound(i);
            rand = PseudoRandom.randDouble();
            beta = 1.0 + (2.0 * (y1 - yL) / (y2 - y1));
            alpha = 2.0 - java.lang.Math.pow(beta, -(distributionIndex_ + 1.0));

            if (rand <= (1.0 / alpha)) {
              betaq = java.lang.Math.pow((rand * alpha), (1.0 / (distributionIndex_ + 1.0)));
            } else {
              betaq = java.lang.Math
                .pow((1.0 / (2.0 - rand * alpha)), (1.0 / (distributionIndex_ + 1.0)));
            }

            c1 = 0.5 * ((y1 + y2) - betaq * (y2 - y1));
            beta = 1.0 + (2.0 * (yu - y2) / (y2 - y1));
            alpha = 2.0 - java.lang.Math.pow(beta, -(distributionIndex_ + 1.0));

            if (rand <= (1.0 / alpha)) {
              betaq = java.lang.Math.pow((rand * alpha), (1.0 / (distributionIndex_ + 1.0)));
            } else {
              betaq = java.lang.Math
                .pow((1.0 / (2.0 - rand * alpha)), (1.0 / (distributionIndex_ + 1.0)));
            }

            c2 = 0.5 * ((y1 + y2) + betaq * (y2 - y1));

            if (c1 < yL) {
              c1 = yL;
            }

            if (c2 < yL) {
              c2 = yL;
            }

            if (c1 > yu) {
              c1 = yu;
            }

            if (c2 > yu) {
              c2 = yu;
            }

            if (PseudoRandom.randDouble() <= 0.5) {
              offs1.setValue(i, c2);
              offs2.setValue(i, c1);
            } else {
              offs1.setValue(i, c1);
              offs2.setValue(i, c2);
            }
          } else {
            offs1.setValue(i, valueX1);
            offs2.setValue(i, valueX2);
          }
        } else {
          offs1.setValue(i, valueX2);
          offs2.setValue(i, valueX1);
        }
      }
    }

    // Single point crossover
    if (PseudoRandom.randDouble() <= binaryProbability) {
      Binary binaryChild0 = (Binary) offSpring[0].getDecisionVariables()[1];
      Binary binaryChild1 = (Binary) offSpring[1].getDecisionVariables()[1];

      int totalNumberOfBits = binaryChild0.getNumberOfBits();

      //2. Calculate the point to make the crossover
      int crossoverPoint = PseudoRandom.randInt(0, totalNumberOfBits - 1);

      //5. Make the crossover;
      for (int i = crossoverPoint; i < totalNumberOfBits; i++) {
        boolean swap = binaryChild0.getBits().get(i);
        binaryChild0.getBits().set(i, binaryChild1.getBits().get(i));
        binaryChild1.getBits().set(i, swap);
      }
    }

    return offSpring;
  }

  @Override
  public Object execute(Object object) throws JMetalException {
    Solution[] parents = (Solution[]) object;

    if (parents.length != 2) {
      throw new JMetalException("SBXSinglePointCrossover.execute: operator " +
        "needs two parents");
    }

    if (!(VALID_TYPES.contains(parents[0].getType().getClass()) &&
      VALID_TYPES.contains(parents[1].getType().getClass()))) {
      throw new JMetalException("SBXSinglePointCrossover.execute: the solutions " +
        "type " + parents[0].getType() + " is not allowed with this operator");
    }

    Solution[] offSpring;
    offSpring = doCrossover(realCrossoverProbability,
            binaryCrossoverProbability, parents[0], parents[1]);

    return offSpring;
  }
}

