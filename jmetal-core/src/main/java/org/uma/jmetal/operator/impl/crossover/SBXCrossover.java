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
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.util.RepairDoubleSolution;
import org.uma.jmetal.solution.util.RepairDoubleSolutionAtBounds;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * This class allows to apply a SBX crossover operator using two parent solutions (Double encoding).
 *
 * The implementation is based on the NSGA-II code available in
 * <a href="http://www.iitk.ac.in/kangal/codes.shtml">http://www.iitk.ac.in/kangal/codes.shtml</a>
 *
 * @author Antonio J. Nebro
 * @author Juan J. Durillo
 * @version 1.0
 */
public class SBXCrossover implements CrossoverOperator<List<DoubleSolution>, List<DoubleSolution>> {
  /** EPS defines the minimum difference allowed between real values */
  private static final double EPS = 1.0e-14;

  private double distributionIndex ;
  private double crossoverProbability  ;
  private RepairDoubleSolution solutionRepair ;

  private JMetalRandom randomGenerator ;

  /** Constructor */
  public SBXCrossover(double crossoverProbability, double distributionIndex) {
    this (crossoverProbability, distributionIndex, new RepairDoubleSolutionAtBounds()) ;
  }

  /** Constructor */
  public SBXCrossover(double crossoverProbability, double distributionIndex, RepairDoubleSolution solutionRepair) {
    if (crossoverProbability < 0) {
      throw new JMetalException("Crossover probability is negative: " + crossoverProbability) ;
    } else if (distributionIndex < 0) {
      throw new JMetalException("Distribution index is negative: " + distributionIndex);
    }

    this.crossoverProbability = crossoverProbability ;
    this.distributionIndex = distributionIndex ;
    this.solutionRepair = solutionRepair ;

    randomGenerator = JMetalRandom.getInstance() ;
  }

  /* Getters */
  public double getCrossoverProbability() {
    return crossoverProbability;
  }

  public double getDistributionIndex() {
    return distributionIndex;
  }

  /** Execute() method */
  @Override
  public List<DoubleSolution> execute(List<DoubleSolution> solutions) {
    if (null == solutions) {
      throw new JMetalException("Null parameter") ;
    } else if (solutions.size() != 2) {
      throw new JMetalException("There must be two parents instead of " + solutions.size()) ;
    }

    return doCrossover(crossoverProbability, solutions.get(0), solutions.get(1)) ;
  }

  /** doCrossover method */
  public List<DoubleSolution> doCrossover(
      double probability, DoubleSolution parent1, DoubleSolution parent2) {
    List<DoubleSolution> offspring = new ArrayList<DoubleSolution>(2);

    offspring.add((DoubleSolution) parent1.copy()) ;
    offspring.add((DoubleSolution) parent2.copy()) ;

    int i;
    double rand;
    double y1, y2, lowerBound, upperBound;
    double c1, c2;
    double alpha, beta, betaq;
    double valueX1, valueX2;

    if (randomGenerator.nextDouble() <= probability) {
      for (i = 0; i < parent1.getNumberOfVariables(); i++) {
        valueX1 = parent1.getVariableValue(i);
        valueX2 = parent2.getVariableValue(i);
        if (randomGenerator.nextDouble() <= 0.5) {
          if (Math.abs(valueX1 - valueX2) > EPS) {

            if (valueX1 < valueX2) {
              y1 = valueX1;
              y2 = valueX2;
            } else {
              y1 = valueX2;
              y2 = valueX1;
            }

            lowerBound = parent1.getLowerBound(i);
            upperBound = parent1.getUpperBound(i);

            rand = randomGenerator.nextDouble();
            beta = 1.0 + (2.0 * (y1 - lowerBound) / (y2 - y1));
            alpha = 2.0 - Math.pow(beta, -(distributionIndex + 1.0));

            if (rand <= (1.0 / alpha)) {
              betaq = Math.pow(rand * alpha, (1.0 / (distributionIndex + 1.0)));
            } else {
              betaq = Math
                  .pow(1.0 / (2.0 - rand * alpha), 1.0 / (distributionIndex + 1.0));
            }
            c1 = 0.5 * (y1 + y2 - betaq * (y2 - y1));

            beta = 1.0 + (2.0 * (upperBound - y2) / (y2 - y1));
            alpha = 2.0 - Math.pow(beta, -(distributionIndex + 1.0));

            if (rand <= (1.0 / alpha)) {
              betaq = Math.pow((rand * alpha), (1.0 / (distributionIndex + 1.0)));
            } else {
              betaq = Math
                  .pow(1.0 / (2.0 - rand * alpha), 1.0 / (distributionIndex + 1.0));
            }
            c2 = 0.5 * (y1 + y2 + betaq * (y2 - y1));

            c1 = solutionRepair.repairSolutionVariableValue(c1, lowerBound, upperBound) ;
            c2 = solutionRepair.repairSolutionVariableValue(c2, lowerBound, upperBound) ;

            if (randomGenerator.nextDouble() <= 0.5) {
              offspring.get(0).setVariableValue(i, c2);
              offspring.get(1).setVariableValue(i, c1);
            } else {
              offspring.get(0).setVariableValue(i, c1);
              offspring.get(1).setVariableValue(i, c2);
            }
          } else {
            offspring.get(0).setVariableValue(i, valueX1);
            offspring.get(1).setVariableValue(i, valueX2);
          }
        } else {
          offspring.get(0).setVariableValue(i, valueX1);
          offspring.get(1).setVariableValue(i, valueX2);
        }
      }
    }

    return offspring;
  }
}
