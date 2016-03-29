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
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * This class implements a non-uniform mutation operator.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class NonUniformMutation implements MutationOperator<DoubleSolution> {
  private double perturbation;
  private int maxIterations;
  private double mutationProbability;

  private int currentIteration;
  private JMetalRandom randomGenenerator ;

  /** Constructor */
  public NonUniformMutation(double mutationProbability, double perturbation, int maxIterations) {
    this.perturbation = perturbation ;
    this.mutationProbability = mutationProbability ;
    this.maxIterations = maxIterations ;

    randomGenenerator = JMetalRandom.getInstance() ;
  }

  /* Getters */
  public double getPerturbation() {
    return perturbation;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public double getMutationProbability() {
    return mutationProbability;
  }

  public int getCurrentIteration() {
    return currentIteration;
  }

  /* Setter */
  public void setCurrentIteration(int currentIteration) {
    if (currentIteration < 0) {
      throw new JMetalException("Iteration number cannot be a negative value: " + currentIteration) ;
    }

    this.currentIteration = currentIteration;
  }

  /** Execute() method */
  @Override
  public DoubleSolution execute(DoubleSolution solution) {
    if (null == solution) {
      throw new JMetalException("Null parameter") ;
    }

    doMutation(mutationProbability, solution);

    return solution;
  }

  /**
   * Perform the mutation operation
   *
   * @param probability Mutation setProbability
   * @param solution    The solution to mutate
   */
  public void doMutation(double probability, DoubleSolution solution){
    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      if (randomGenenerator.nextDouble() < probability) {
        double rand = randomGenenerator.nextDouble();
        double tmp;

        if (rand <= 0.5) {
          tmp = delta(solution.getUpperBound(i) - solution.getVariableValue(i),
              perturbation);
          tmp += solution.getVariableValue(i);
        } else {
          tmp = delta(solution.getLowerBound(i) - solution.getVariableValue(i),
              perturbation);
          tmp += solution.getVariableValue(i);
        }

        if (tmp < solution.getLowerBound(i)) {
          tmp = solution.getLowerBound(i);
        } else if (tmp > solution.getUpperBound(i)) {
          tmp = solution.getUpperBound(i);
        }
        solution.setVariableValue(i, tmp);
      }
    }
  }


  /** Calculates the delta value used in NonUniform mutation operator */
  private double delta(double y, double bMutationParameter) {
    double rand = randomGenenerator.nextDouble();
    int it, maxIt;
    it = currentIteration;
    maxIt = maxIterations;

    return (y * (1.0 -
        Math.pow(rand,
            Math.pow((1.0 - it / (double) maxIt), bMutationParameter)
        )));
  }
}
