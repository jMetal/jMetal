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

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * This class implements a polynomial mutation operator
 *
 * The implementation is based on the NSGA-II code available in
 * http://www.iitk.ac.in/kangal/codes.shtml
 *
 * If the lower and upper bounds of a variable are the same, no mutation is carried out and the
 * bound value is returned.
 *
 * @author Feng Zhang
 */
@SuppressWarnings("serial")
public class CDGMutation implements MutationOperator<DoubleSolution> {
  private static final double DEFAULT_PROBABILITY = 0.01 ;
  private static final double DEFAULT_DELTA = 0.5 ;
  private double delta ;
  private double mutationProbability ;
  private RepairDoubleSolution solutionRepair ;

  private JMetalRandom randomGenerator ;

  /** Constructor */
  public CDGMutation() {
    this(DEFAULT_PROBABILITY, DEFAULT_DELTA) ;
  }

  /** Constructor */
  public CDGMutation(DoubleProblem problem, double delta) {
    this(1.0/problem.getNumberOfVariables(), delta) ;
  }

  /** Constructor */
  public CDGMutation(double mutationProbability, double delta) {
    this(mutationProbability, delta, new RepairDoubleSolutionWithBoundValue()) ;
  }

  /** Constructor */
  public CDGMutation(double mutationProbability, double delta,
      RepairDoubleSolution solutionRepair) {
    if (mutationProbability < 0) {
      throw new JMetalException("Mutation probability is negative: " + mutationProbability) ;
    } else if (delta < 0) {
      throw new JMetalException("Distribution index is negative: " + delta) ;
    }
    this.mutationProbability = mutationProbability;
    this.delta = delta;
    this.solutionRepair = solutionRepair ;

    randomGenerator = JMetalRandom.getInstance() ;
  }

  /* Getters */
  @Override
  public double getMutationProbability() {
    return mutationProbability;
  }

  public double getDelta() {
    return delta;
  }

  /* Setters */
  public void setMutationProbability(double probability) {
    this.mutationProbability = probability ;
  }

  public void setDelta(double delta) {
    this.delta = delta ;
  }

  /** Execute() method */
  @Override
  public @NotNull DoubleSolution execute(DoubleSolution solution) throws JMetalException {
    if (null == solution) {
      throw new JMetalException("Null parameter") ;
    }

    doMutation(mutationProbability, solution);
    return solution;
  }

  /** Perform the mutation operation */
  private void doMutation(double probability, @NotNull DoubleSolution solution) {
    double rnd, deltaq, tempDelta;
    double y, yl, yu;

    for (var i = 0; i < solution.variables().size(); i++) {
      if (randomGenerator.nextDouble() <= probability) {
        y = solution.variables().get(i);
        var bounds = solution.getBounds(i);
        yl = bounds.getLowerBound() ;
        yu = bounds.getUpperBound() ;
        rnd = randomGenerator.nextDouble();
          
        tempDelta = Math.pow(rnd, -delta);
        deltaq = 0.5 * (rnd - 0.5) * (1 - tempDelta);
          
        y = y + deltaq * (yu - yl);
        y = solutionRepair.repairSolutionVariableValue(y, yl, yu);
        solution.variables().set(i, y);
      }
    }
  }
}
