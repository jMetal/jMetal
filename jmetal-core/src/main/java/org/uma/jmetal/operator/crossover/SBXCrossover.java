//  SBXCrossover.java
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
import org.uma.jmetal.encoding.solutiontype.ArrayIntSolutionType;
import org.uma.jmetal.encoding.solutiontype.ArrayRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.IntSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal.util.wrapper.XInt;
import org.uma.jmetal.util.wrapper.XReal;

import java.util.HashMap;

/**
 * This class allows to apply a SBX crossover operator using two parent
 * solutions.
 */
public class SBXCrossover extends Crossover {
  private static final long serialVersionUID = -880076874462438998L;

  /** EPS defines the minimum difference allowed between real values */
  private static final double EPS = 1.0e-14;

  private static final double ETA_C_DEFAULT = 20.0;
  private static final double DEFAULT_PROBABILITY = 0.9 ;

  private double distributionIndex = ETA_C_DEFAULT;
  private double crossoverProbability = DEFAULT_PROBABILITY;

  /**
   * Constructor
   * Create a new SBX crossover operator whit a default
   * index given by <code>DEFAULT_INDEX_CROSSOVER</code>
   */
  @Deprecated
  public SBXCrossover(HashMap<String, Object> parameters) {
    super(parameters);

    addValidSolutionType(RealSolutionType.class);
    addValidSolutionType(ArrayRealSolutionType.class);

    if (parameters.get("probability") != null) {
      crossoverProbability = (Double) parameters.get("probability");
    }
    if (parameters.get("distributionIndex") != null) {
      distributionIndex= (Double) parameters.get("distributionIndex");
    }
  }

  /** Constructor */
  private SBXCrossover(Builder builder) {
    addValidSolutionType(RealSolutionType.class);
    addValidSolutionType(ArrayRealSolutionType.class);

    addValidSolutionType(IntSolutionType.class);
    addValidSolutionType(ArrayIntSolutionType.class);

    crossoverProbability = builder.crossoverProbability ;
    distributionIndex = builder.distributionIndex ;
  }

  /** Getters */
  public double getCrossoverProbability() {
    return crossoverProbability;
  }
  public double getDistributionIndex() {
    return distributionIndex;
  }

  /** Execute method */
  public Object execute(Object object) throws JMetalException {
    Solution[] parents = (Solution[]) object;

    if (parents.length != 2) {
      throw new JMetalException("SBXCrossover.execute: operator needs two " +
        "parents");
    }

    if (!solutionTypeIsValid(parents)) {
      throw new JMetalException("SBXCrossover.execute: the solutions " +
        "type " + parents[0].getType() + " is not allowed with this operator");
    }

    Solution[] offSpring;
    offSpring = doCrossover(crossoverProbability, parents[0], parents[1]);

    return offSpring;
  }

  /** doCrossover method */
  public Solution[] doCrossover(double probability, Solution parent1, Solution parent2) throws JMetalException {
    SolutionType type = parent1.getType();
    if ((type instanceof RealSolutionType) || (type instanceof ArrayRealSolutionType)) {
      return doRealCrossover(probability, parent1, parent2);
    } else {
      return doIntegerCrossover(probability, parent1, parent2) ;
    }
  }

  /** Crossover operation (real encoding) */
  public Solution[] doRealCrossover(double probability,
    Solution parent1,
    Solution parent2) throws JMetalException {

    Solution[] offSpring = new Solution[2];

    offSpring[0] = new Solution(parent1);
    offSpring[1] = new Solution(parent2);

    int i;
    double rand;
    double y1, y2, yL, yu;
    double c1, c2;
    double alpha, beta, betaq;
    double valueX1, valueX2;
    XReal x1 = new XReal(parent1);
    XReal x2 = new XReal(parent2);
    XReal offs1 = new XReal(offSpring[0]);
    XReal offs2 = new XReal(offSpring[1]);

    int numberOfVariables = x1.getNumberOfDecisionVariables();

    if (PseudoRandom.randDouble() <= probability) {
      for (i = 0; i < numberOfVariables; i++) {
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
            alpha = 2.0 - java.lang.Math.pow(beta, -(distributionIndex + 1.0));

            if (rand <= (1.0 / alpha)) {
              betaq = java.lang.Math.pow((rand * alpha), (1.0 / (distributionIndex + 1.0)));
            } else {
              betaq = java.lang.Math
                .pow((1.0 / (2.0 - rand * alpha)), (1.0 / (distributionIndex + 1.0)));
            }

            c1 = 0.5 * ((y1 + y2) - betaq * (y2 - y1));
            beta = 1.0 + (2.0 * (yu - y2) / (y2 - y1));
            alpha = 2.0 - java.lang.Math.pow(beta, -(distributionIndex + 1.0));

            if (rand <= (1.0 / alpha)) {
              betaq = java.lang.Math.pow((rand * alpha), (1.0 / (distributionIndex + 1.0)));
            } else {
              betaq = java.lang.Math
                .pow((1.0 / (2.0 - rand * alpha)), (1.0 / (distributionIndex + 1.0)));
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

    return offSpring;
  }

  /** Crossover operation (integer encoding) */
  public Solution[] doIntegerCrossover(double probability,
    Solution parent1,
    Solution parent2) throws JMetalException {

    Solution[] offSpring = new Solution[2];

    offSpring[0] = new Solution(parent1);
    offSpring[1] = new Solution(parent2);

    int i;
    double rand;
    double y1, y2, yL, yu;
    double c1, c2;
    double alpha, beta, betaq;
    double valueX1, valueX2;
    XInt x1 = new XInt(parent1);
    XInt x2 = new XInt(parent2);
    XInt offs1 = new XInt(offSpring[0]);
    XInt offs2 = new XInt(offSpring[1]);

    int numberOfVariables = x1.getNumberOfDecisionVariables();

    if (PseudoRandom.randDouble() <= probability) {
      for (i = 0; i < numberOfVariables; i++) {
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
            alpha = 2.0 - java.lang.Math.pow(beta, -(distributionIndex + 1.0));

            if (rand <= (1.0 / alpha)) {
              betaq = java.lang.Math.pow((rand * alpha), (1.0 / (distributionIndex + 1.0)));
            } else {
              betaq = java.lang.Math
                .pow((1.0 / (2.0 - rand * alpha)), (1.0 / (distributionIndex + 1.0)));
            }

            c1 = 0.5 * ((y1 + y2) - betaq * (y2 - y1));
            beta = 1.0 + (2.0 * (yu - y2) / (y2 - y1));
            alpha = 2.0 - java.lang.Math.pow(beta, -(distributionIndex + 1.0));

            if (rand <= (1.0 / alpha)) {
              betaq = java.lang.Math.pow((rand * alpha), (1.0 / (distributionIndex + 1.0)));
            } else {
              betaq = java.lang.Math
                .pow((1.0 / (2.0 - rand * alpha)), (1.0 / (distributionIndex + 1.0)));
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
              offs1.setValue(i, (int)c2);
              offs2.setValue(i, (int)c1);
            } else {
              offs1.setValue(i, (int)c1);
              offs2.setValue(i, (int)c2);
            }
          } else {
            offs1.setValue(i, (int)valueX1);
            offs2.setValue(i, (int)valueX2);
          }
        } else {
          offs1.setValue(i, (int)valueX2);
          offs2.setValue(i, (int)valueX1);
        }
      }
    }

    return offSpring;
  }


  /** Builder class */
  public static class Builder {
    private double distributionIndex ;
    private double crossoverProbability ;

    public Builder() {
      distributionIndex = ETA_C_DEFAULT ;
      crossoverProbability = DEFAULT_PROBABILITY ;
    }

    public Builder distributionIndex(double distributionIndex) {
      if (distributionIndex < 0) {
        throw new JMetalException("Distribution index invalid: " + distributionIndex) ;
      } else {
        distributionIndex = distributionIndex;
      }

      return this ;
    }

    public Builder probability(double probability) {
      if ((probability < 0) || (probability > 1.0)) {
        throw new JMetalException("Probability value invalid: " + probability) ;
      } else {
        crossoverProbability = probability;
      }

      return this ;
    }

    public SBXCrossover build() {
      return new SBXCrossover(this) ;
    }
  }
}
