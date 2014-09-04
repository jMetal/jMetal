//  PolynomialMutation.java
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

package org.uma.jmetal3.operator.impl;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal3.encoding.NumericSolution;
import org.uma.jmetal3.operator.IndividualOperator;

/** This class implements a polynomial mutation operator */
public class PolynomialMutation implements IndividualOperator<NumericSolution<Double>, NumericSolution<Double>> {
  private static final double ETA_M_DEFAULT = 20.0;
  private double distributionIndex ;

  private double mutationProbability ;

  /** Constructor */
  private PolynomialMutation(Builder builder) {
    mutationProbability = builder.mutationProbability;
    distributionIndex = builder.distributionIndex;
  }

  /* Getters */
  public double getMutationProbability() {
    return mutationProbability;
  }

  public double getDistributionIndex() {
    return distributionIndex;
  }

  /** Builder class */
  public static class Builder {
    private double distributionIndex;
    private double mutationProbability;

    public Builder() {
      distributionIndex = ETA_M_DEFAULT;
      mutationProbability = 0.0 ;
    }

    public Builder setDistributionIndex(double distributionIndex) {
      this.distributionIndex = distributionIndex ;

      return this ;
    }

    public Builder setProbability(double probability) {
      mutationProbability = probability ;

      return this ;
    }

    public PolynomialMutation build() {
      return new PolynomialMutation(this) ;
    }
  }

  /** Execute() method */
  public NumericSolution execute(NumericSolution object) throws JMetalException {
    if (null == object) {
      throw new JMetalException("Null parameter") ;
    }

    NumericSolution<Double> solution = (NumericSolution<Double>) object;

    doMutation(mutationProbability, solution);
    return solution;
  }

  /** Perform the mutation operation */
  public void doMutation(double probability, NumericSolution<Double> solution) throws JMetalException {
    //SolutionType type = solution.getType();
    //if ((type instanceof RealSolutionType) || (type instanceof ArrayRealSolutionType)) {
      doRealMutation(probability, solution);
    //} else {
    //  doIntegerMutation(probability, solution) ;
    //}
  }

  private void doRealMutation(double probability, NumericSolution<Double> solution) {
    Double rnd, delta1, delta2, mutPow, deltaq;
    Double y, yl, yu, val, xy;

    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      if (PseudoRandom.randDouble() <= probability) {
        y = (Double)solution.getVariableValue(i); //x.getValue(var);
        yl = solution.getLowerBound(i) ;//x.getLowerBound(var);
        yu = solution.getUpperBound(i) ;//x.getUpperBound(var);
        delta1 = (y - yl) / (yu - yl);
        delta2 = (yu - y) / (yu - yl);
        rnd = PseudoRandom.randDouble();
        mutPow = 1.0 / (distributionIndex + 1.0);
        if (rnd <= 0.5) {
          xy = 1.0 - delta1;
          val = 2.0 * rnd + (1.0 - 2.0 * rnd) * (Math.pow(xy, distributionIndex + 1.0));
          deltaq = Math.pow(val, mutPow) - 1.0;
        } else {
          xy = 1.0 - delta2;
          val =
            2.0 * (1.0 - rnd) + 2.0 * (rnd - 0.5) * (Math.pow(xy, distributionIndex + 1.0));
          deltaq = 1.0 - Math.pow(val, mutPow);
        }
        y = y + deltaq * (yu - yl);
        if (y < yl) {
          y = yl;
        }
        if (y > yu) {
          y = yu;
        }
        solution.setVariableValue(i, y);
      }
    }
  }

//  private void doIntegerMutation(double probability, Solution solution) {
//
//  }
}
