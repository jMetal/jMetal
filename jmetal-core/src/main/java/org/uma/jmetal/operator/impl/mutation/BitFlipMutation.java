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
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @version 1.0
 *
 * This class implements a bit flip mutation operator.
 */
public class BitFlipMutation implements MutationOperator<BinarySolution> {
  private double mutationProbability ;
  private JMetalRandom randomGenerator ;

  /** Constructor */
  public BitFlipMutation(double mutationProbability) {
    if (mutationProbability < 0) {
      throw new JMetalException("Mutation probability is negative: " + mutationProbability) ;
    }
    this.mutationProbability = mutationProbability;
    randomGenerator = JMetalRandom.getInstance() ;
  }

  /* Getter */
  public double getMutationProbability() {
    return mutationProbability;
  }

  /** Execute() method */
  @Override
  public BinarySolution execute(BinarySolution solution) {
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
  public void doMutation(double probability, BinarySolution solution)  {
    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      for (int j = 0; j < solution.getVariableValue(i).getBinarySetLength(); j++) {
        if (randomGenerator.nextDouble() <= probability) {
          solution.getVariableValue(i).flip(j);
        }
      }
    }
  }
}
