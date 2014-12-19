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
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * This class allows to apply a HUX crossover operator using two parent
 * solutions.
 * NOTE: the operator is applied to the first encoding.variable of the solutions, and
 * the type of the solutions must be Binary
 *
 * @author Antonio J. Nebro
 * @author Juan J. Durillo
 * @version 1.0
 */
public class HUXCrossover implements CrossoverOperator<List<BinarySolution>, List<BinarySolution>> {
  private double crossoverProbability ;
  private JMetalRandom randomGenerator ;

  /** Constructor */
  public HUXCrossover(double crossoverProbability) {
    if (crossoverProbability < 0) {
      throw new JMetalException("Crossover probability is negative: " + crossoverProbability) ;
    }
    this.crossoverProbability = crossoverProbability ;
    randomGenerator = JMetalRandom.getInstance() ;
  }

  /* Getter */
  public double getCrossoverProbability() {
    return this.crossoverProbability;
  }

  /** Execute() method */
  public List<BinarySolution> execute(List<BinarySolution> parents) {
    if (parents.size() != 2) {
      throw new JMetalException("HUXCrossover.execute: operator needs two parents");
    }

    return doCrossover(crossoverProbability, parents.get(0), parents.get(1));
  }

  /**
   * Perform the crossover operation
   *
   * @param probability Crossover setProbability
   * @param parent1     The first parent
   * @param parent2     The second parent
   * @return An array containing the two offspring
   * @throws org.uma.jmetal.util.JMetalException
   */
  public List<BinarySolution> doCrossover(double probability,
                                          BinarySolution parent1,
                                          BinarySolution parent2) throws JMetalException {
    if (randomGenerator.nextDouble() < probability) {
      for (int var = 0; var < parent1.getNumberOfVariables(); var++) {
        BitSet p1 = parent1.getVariableValue(var) ;
        BitSet p2 = parent1.getVariableValue(var) ;

        for (int bit = 0; bit < parent1.getNumberOfBits(var); bit++) {
          if ((p1.get(bit) != p2.get(bit)) && (randomGenerator.nextDouble() < 0.5)) {
            parent1.getVariableValue(var).set(bit, p2.get(bit)) ;
            parent2.getVariableValue(var).set(bit, p1.get(bit)) ;
          }
        }
      }
    }

    List<BinarySolution> offSpring = new ArrayList<>();
    offSpring.add(parent1) ;
    offSpring.add(parent2) ;

    return offSpring;
  }
}
