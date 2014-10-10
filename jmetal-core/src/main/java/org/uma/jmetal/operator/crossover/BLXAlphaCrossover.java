//  BLXAlphaCrossover.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//
//  Copyright (c) 2012 Antonio J. Nebro
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
import org.uma.jmetal.encoding.solutiontype.ArrayRealSolutionType;
import org.uma.jmetal.encoding.solutiontype.RealSolutionType;
import org.uma.jmetal.encoding.solutiontype.wrapper.XReal;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.HashMap;

/**
 * This class allows to apply a SBX crossover operator using two parent
 * solutions.
 */
public class BLXAlphaCrossover extends Crossover {
  private static final long serialVersionUID = -7738534841007212922L;

  private static final double DEFAULT_ALPHA = 0.5;
  private double alpha = DEFAULT_ALPHA;

  private double crossoverProbability;

  @Deprecated
  public BLXAlphaCrossover(HashMap<String, Object> parameters) {
    super(parameters);

    if (parameters.get("setProbability") != null) {
      crossoverProbability = (Double) parameters.get("setProbability");
    }
    if (parameters.get("alpha") != null) {
      alpha = (Double) parameters.get("alpha");
    }

    addValidSolutionType(RealSolutionType.class);
    addValidSolutionType(ArrayRealSolutionType.class);
  }

  /** Constructor */
  private BLXAlphaCrossover(Builder builder) {
    addValidSolutionType(RealSolutionType.class);
    addValidSolutionType(ArrayRealSolutionType.class);

    crossoverProbability = builder.crossoverProbability;
    alpha = builder.alpha;
  }

  /* Getters */
  public double getCrossoverProbability() {
    return crossoverProbability;
  }

  public double getAlpha() {
    return alpha;
  }

  /** Builder class */
  public static class Builder {
    private double alpha;
    private double crossoverProbability;

    public Builder() {
      alpha = DEFAULT_ALPHA ;
      crossoverProbability = 0.9 ;
    }

    public Builder probability(double probability) {
      if ((probability < 0) || (probability > 1.0)) {
        throw new JMetalException("Probability value invalid: " + probability) ;
      } else {
        crossoverProbability = probability;
      }

      return this ;
    }

    public Builder getAlpha(double alpha) {
      this.alpha = alpha;

      return this ;
    }

    public BLXAlphaCrossover build() {
      return new BLXAlphaCrossover(this) ;
    }
  }

  /** Execute() method */
  public Object execute(Object object) throws JMetalException {
    Solution[] parents = (Solution[]) object;

    if (parents.length != 2) {
      throw new JMetalException("BLXAlphaCrossover.execute: operator needs two parents");
    }

    if (!solutionTypeIsValid(parents)) {
      throw new JMetalException("BLXAlphaCrossover.execute: the solutions " +
              "type " + parents[0].getType() + " is not allowed with this operator");
    }

    Solution[] offSpring;
    offSpring = doCrossover(crossoverProbability,
            parents[0],
            parents[1]);

    return offSpring;
  }

  /** Perform the crossover operation */
  public Solution[] doCrossover(double probability,
    Solution parent1,
    Solution parent2) throws JMetalException {

    Solution[] offSpring = new Solution[2];

    offSpring[0] = new Solution(parent1);
    offSpring[1] = new Solution(parent2);

    int i;
    double random;
    double valueY1;
    double valueY2;
    double valueX1;
    double valueX2;
    double upperValue;
    double lowerValue;

    XReal x1 = new XReal(parent1);
    XReal x2 = new XReal(parent2);
    XReal offs1 = new XReal(offSpring[0]);
    XReal offs2 = new XReal(offSpring[1]);

    int numberOfVariables = x1.getNumberOfDecisionVariables();

    if (PseudoRandom.randDouble() <= probability) {
      for (i = 0; i < numberOfVariables; i++) {
        upperValue = x1.getUpperBound(i);
        lowerValue = x1.getLowerBound(i);
        valueX1 = x1.getValue(i);
        valueX2 = x2.getValue(i);

        double max;
        double min;
        double range;

        if (valueX2 > valueX1) {
          max = valueX2;
          min = valueX1;
        } else {
          max = valueX1;
          min = valueX2;
        }

        range = max - min;
        // Ranges of the new alleles ;
        double minRange;
        double maxRange;

        minRange = min - range * alpha;
        maxRange = max + range * alpha;

        random = PseudoRandom.randDouble();
        valueY1 = minRange + random * (maxRange - minRange);

        random = PseudoRandom.randDouble();
        valueY2 = minRange + random * (maxRange - minRange);

        if (valueY1 < lowerValue) {
          offs1.setValue(i, lowerValue);
        } else if (valueY1 > upperValue) {
          offs1.setValue(i, upperValue);
        } else {
          offs1.setValue(i, valueY1);
        }

        if (valueY2 < lowerValue) {
          offs2.setValue(i, lowerValue);
        } else if (valueY2 > upperValue) {
          offs2.setValue(i, upperValue);
        } else {
          offs2.setValue(i, valueY2);
        }
      }
    }

    return offSpring;
  }
}
