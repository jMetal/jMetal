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
 * This class implements a uniform mutation operator.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class UniformMutation implements MutationOperator<DoubleSolution> {
  private Double perturbation;
  private Double mutationProbability = null;
  private JMetalRandom randomGenenerator ;

  /** Constructor */
  public UniformMutation(double mutationProbability, double perturbation) {
    this.mutationProbability = mutationProbability ;
    this.perturbation = perturbation ;
    randomGenenerator = JMetalRandom.getInstance() ;
  }

  /* Getters */
  public Double getPerturbation() {
    return perturbation;
  }

  public Double getMutationProbability() {
    return mutationProbability;
  }

  /**
   * Perform the operation
   *
   * @param probability Mutation setProbability
   * @param solution    The solution to mutate
   */
  public void doMutation(double probability, DoubleSolution solution)  {
    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      if (randomGenenerator.nextDouble() < probability) {
        double rand = randomGenenerator.nextDouble();
        double tmp = (rand - 0.5) * perturbation;

        tmp += solution.getVariableValue(i);

        if (tmp < solution.getLowerBound(i)) {
          tmp = solution.getLowerBound(i);
        } else if (tmp > solution.getUpperBound(i)) {
          tmp = solution.getUpperBound(i);
        }

        solution.setVariableValue(i, tmp);
      }
    }
  }

  /** Execute() method */
  @Override
  public DoubleSolution execute(DoubleSolution solution) {
    if (null == solution) {
      throw new JMetalException("Null parameter");
    }

    doMutation(mutationProbability, solution);

    return solution;
  }
}
