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

package org.uma.jmetal.operator.impl.mutation;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/** This class implements a polynomial mutation operator */
public class IntegerPolynomialMutation implements MutationOperator<IntegerSolution> {
  private static final double ETA_M_DEFAULT = 20.0;
  private double distributionIndex ;
  private double mutationProbability ;

  private JMetalRandom randomGenerator ;

  /** Constructor */
  public IntegerPolynomialMutation(double mutationProbability, double distributionIndex) {
    this.mutationProbability = mutationProbability;
    this.distributionIndex = distributionIndex;
    randomGenerator = JMetalRandom.getInstance() ;
  }

  /* Getters */
  public double getMutationProbability() {
    return mutationProbability;
  }

  public double getDistributionIndex() {
    return distributionIndex;
  }

  /** Execute() method */
  public IntegerSolution execute(IntegerSolution solution) throws JMetalException {
    if (null == solution) {
      throw new JMetalException("Null parameter") ;
    }

    doMutation(mutationProbability, solution);
    return solution;
  }

  /** Perform the mutation operation */
  public void doMutation(double probability, IntegerSolution solution) throws JMetalException {
    doRealMutation(probability, solution);
  }

  private void doRealMutation(double probability, IntegerSolution solution) {
    Double rnd, delta1, delta2, mutPow, deltaq;
    Double y, yl, yu, val, xy;

    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      if (randomGenerator.nextDouble() <= probability) {
        y = (double)solution.getVariableValue(i);
        yl = (double)solution.getLowerBound(i) ;
        yu = (double)solution.getUpperBound(i) ;
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
        solution.setVariableValue(i, y.intValue());
      }
    }
  }
}
