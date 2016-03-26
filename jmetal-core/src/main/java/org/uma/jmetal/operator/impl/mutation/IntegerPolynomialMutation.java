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
import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.util.RepairDoubleSolution;
import org.uma.jmetal.solution.util.RepairDoubleSolutionAtBounds;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * This class implements a polynomial mutation operator to be applied to Integer solutions
 *
 * If the lower and upper bounds of a variable are the same, no mutation is carried out and the
 * bound value is returned.
 *
 * A {@link RepairDoubleSolution} object is used to decide the strategy to apply when a value is out
 * of range.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class IntegerPolynomialMutation implements MutationOperator<IntegerSolution> {
  private static final double DEFAULT_PROBABILITY = 0.01 ;
  private static final double DEFAULT_DISTRIBUTION_INDEX = 20.0 ;

  private double distributionIndex ;
  private double mutationProbability ;
  private RepairDoubleSolution solutionRepair ;

  private JMetalRandom randomGenerator ;

  /** Constructor */
  public IntegerPolynomialMutation() {
    this(DEFAULT_PROBABILITY, DEFAULT_DISTRIBUTION_INDEX) ;
  }

  /** Constructor */
  public IntegerPolynomialMutation(IntegerProblem problem, double distributionIndex) {
    this(1.0/problem.getNumberOfVariables(), distributionIndex) ;
  }

  /** Constructor */
  public IntegerPolynomialMutation(double mutationProbability, double distributionIndex) {
    this(mutationProbability, distributionIndex, new RepairDoubleSolutionAtBounds()) ;
  }

  /** Constructor */
  public IntegerPolynomialMutation(double mutationProbability, double distributionIndex,
      RepairDoubleSolution solutionRepair) {
    if (mutationProbability < 0) {
      throw new JMetalException("Mutation probability is negative: " + mutationProbability) ;
    } else if (distributionIndex < 0) {
      throw new JMetalException("Distribution index is negative: " + distributionIndex);
    }
    this.mutationProbability = mutationProbability;
    this.distributionIndex = distributionIndex;
    this.solutionRepair  = solutionRepair ;

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
  private void doMutation(double probability, IntegerSolution solution) {
    Double rnd, delta1, delta2, mutPow, deltaq;
    double y, yl, yu, val, xy;

    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      if (randomGenerator.nextDouble() <= probability) {
        y = (double)solution.getVariableValue(i);
        yl = (double)solution.getLowerBound(i) ;
        yu = (double)solution.getUpperBound(i) ;
        if (yl == yu) {
          y = yl ;
        } else {
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
            val = 2.0 * (1.0 - rnd) + 2.0 * (rnd - 0.5) * (Math.pow(xy, distributionIndex + 1.0));
            deltaq = 1.0 - Math.pow(val, mutPow);
          }
          y = y + deltaq * (yu - yl);
          y = solutionRepair.repairSolutionVariableValue(y, yl, yu);
        }
        solution.setVariableValue(i, (int) y);
      }
    }
  }
}
