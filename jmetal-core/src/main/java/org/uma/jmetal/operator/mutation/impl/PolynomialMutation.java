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

package org.uma.jmetal.operator.mutation.impl;

import org.uma.jmetal.encoding.DoubleSolution;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/** This class implements a polynomial mutation operator */
public class PolynomialMutation implements MutationOperator<DoubleSolution> {
  private static final double ETA_M_DEFAULT = 20.0;
  private double distributionIndex ;
  private double mutationProbability ;

  private JMetalRandom randomGenerator ;

  /** Constructor */
  public PolynomialMutation(double mutationProbability, double distributionIndex) {
    this.mutationProbability = mutationProbability;
    this.distributionIndex = distributionIndex;
    randomGenerator = JMetalRandom.getInstance() ;
  }

  /** Constructor */
  public PolynomialMutation() {
    this(0.01, 20.0) ;
  }

  /** Constructor */
  private PolynomialMutation(Builder builder) {
    this(builder.mutationProbability, builder.distributionIndex) ;
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
      mutationProbability = 0.01 ;
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
  public DoubleSolution execute(DoubleSolution solution) throws JMetalException {
    if (null == solution) {
      throw new JMetalException("Null parameter") ;
    }

    doMutation(mutationProbability, solution);
    return solution;
  }

  /** Perform the mutation operation */
  public void doMutation(double probability, DoubleSolution solution) throws JMetalException {
    doRealMutation(probability, solution);
  }

  private void doRealMutation(double probability, DoubleSolution solution) {
    Double rnd, delta1, delta2, mutPow, deltaq;
    Double y, yl, yu, val, xy;

    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      if (randomGenerator.nextDouble() <= probability) {
        y = solution.getVariableValue(i);
        yl = solution.getLowerBound(i) ;
        yu = solution.getUpperBound(i) ;
        delta1 = (y - yl) / (yu - yl);
        delta2 = (yu - y) / (yu - yl);
        rnd = randomGenerator.nextDouble();
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
}
