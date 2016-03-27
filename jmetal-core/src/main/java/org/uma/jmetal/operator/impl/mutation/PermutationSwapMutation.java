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
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * This class implements a swap mutation. The solution type of the solution
 * must be Permutation.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class PermutationSwapMutation<T> implements MutationOperator<PermutationSolution<T>> {
  private double mutationProbability ;
  private JMetalRandom randomGenerator ;

  /**
   * Constructor
   */
  public PermutationSwapMutation(double mutationProbability) {
    if ((mutationProbability < 0) || (mutationProbability > 1)) {
      throw new JMetalException("Mutation probability value invalid: " + mutationProbability) ;
    }
    this.mutationProbability = mutationProbability;
    randomGenerator = JMetalRandom.getInstance() ;
  }

  /** Execute() method */
  @Override
  public PermutationSolution<T> execute(PermutationSolution<T> solution) {
    if (null == solution) {
      throw new JMetalException("Null parameter") ;
    }

    doMutation(solution);
    return solution;
  }

  /**
   * Performs the operation
   */
  public void doMutation(PermutationSolution<T> solution) {
    int permutationLength ;
    permutationLength = solution.getNumberOfVariables() ;

    if ((permutationLength != 0) && (permutationLength != 1)) {
      if (randomGenerator.nextDouble() < mutationProbability) {
        int pos1 = randomGenerator.nextInt(0, permutationLength - 1);
        int pos2 = randomGenerator.nextInt(0, permutationLength - 1);

        while (pos1 == pos2) {
          if (pos1 == (permutationLength - 1))
            pos2 = randomGenerator.nextInt(0, permutationLength - 2);
          else
            pos2 = randomGenerator.nextInt(pos1, permutationLength - 1);
        }

        T temp = solution.getVariableValue(pos1);
        solution.setVariableValue(pos1, solution.getVariableValue(pos2));
        solution.setVariableValue(pos2, temp);
      }
    }
  }
}
