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

package org.uma.jmetal.operator.impl.crossover;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * This class allows to apply a PMX crossover operator using two parent solutions.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
public class PMXCrossover implements
    CrossoverOperator<PermutationSolution<Integer>> {
  private double crossoverProbability = 1.0;
  private JMetalRandom randomGenerator ;

  /**
   * Constructor
   */
  public PMXCrossover(double crossoverProbability) {
    if ((crossoverProbability < 0) || (crossoverProbability > 1)) {
      throw new JMetalException("Crossover probability value invalid: " + crossoverProbability) ;
    }
    this.crossoverProbability = crossoverProbability;
    randomGenerator = JMetalRandom.getInstance() ;
  }

  /**
   * Executes the operation
   *
   * @param parents An object containing an array of two solutions
   */
  public List<PermutationSolution<Integer>> execute(List<PermutationSolution<Integer>> parents) {
    if (null == parents) {
      throw new JMetalException("Null parameter") ;
    } else if (parents.size() != 2) {
      throw new JMetalException("There must be two parents instead of " + parents.size()) ;
    }

    return doCrossover(crossoverProbability, parents) ;
  }

  /**
   * Perform the crossover operation
   *
   * @param probability Crossover probability
   * @param parents     Parents
   * @return An array containing the two offspring
   */
  public List<PermutationSolution<Integer>> doCrossover(double probability, List<PermutationSolution<Integer>> parents) {
    List<PermutationSolution<Integer>> offspring = new ArrayList<>(2);

    offspring.add((PermutationSolution<Integer>) parents.get(0).copy()) ;
    offspring.add((PermutationSolution<Integer>) parents.get(1).copy()) ;

    int permutationLength = parents.get(0).getNumberOfVariables() ;

    if (randomGenerator.nextDouble() < probability) {
      int cuttingPoint1;
      int cuttingPoint2;

      // STEP 1: Get two cutting points
      cuttingPoint1 = randomGenerator.nextInt(0, permutationLength - 1);
      cuttingPoint2 = randomGenerator.nextInt(0, permutationLength - 1);
      while (cuttingPoint2 == cuttingPoint1)
        cuttingPoint2 = randomGenerator.nextInt(0, permutationLength - 1);

      if (cuttingPoint1 > cuttingPoint2) {
        int swap;
        swap = cuttingPoint1;
        cuttingPoint1 = cuttingPoint2;
        cuttingPoint2 = swap;
      }

      // STEP 2: Get the subchains to interchange
      int replacement1[] = new int[permutationLength];
      int replacement2[] = new int[permutationLength];
      for (int i = 0; i < permutationLength; i++)
        replacement1[i] = replacement2[i] = -1;

      // STEP 3: Interchange
      for (int i = cuttingPoint1; i <= cuttingPoint2; i++) {
        offspring.get(0).setVariableValue(i, parents.get(1).getVariableValue(i));
        offspring.get(1).setVariableValue(i, parents.get(0).getVariableValue(i));

        replacement1[parents.get(1).getVariableValue(i)] = parents.get(0).getVariableValue(i) ;
        replacement2[parents.get(0).getVariableValue(i)] = parents.get(1).getVariableValue(i) ;
      }

      // STEP 4: Repair offspring
      for (int i = 0; i < permutationLength; i++) {
        if ((i >= cuttingPoint1) && (i <= cuttingPoint2))
          continue;

        int n1 = parents.get(0).getVariableValue(i);
        int m1 = replacement1[n1];

        int n2 = parents.get(1).getVariableValue(i);
        int m2 = replacement2[n2];

        while (m1 != -1) {
          n1 = m1;
          m1 = replacement1[m1];
        }

        while (m2 != -1) {
          n2 = m2;
          m2 = replacement2[m2];
        }

        offspring.get(0).setVariableValue(i, n1);
        offspring.get(1).setVariableValue(i, n2);
      }
    }

    return offspring;
  }

  /**
   * Two parents are required to apply this operator.
   * @return
   */
  public int getNumberOfParents() {
    return 2 ;
  }
}
