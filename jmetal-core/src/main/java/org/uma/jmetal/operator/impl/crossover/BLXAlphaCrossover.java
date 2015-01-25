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
 * This class allows to apply a BLX-alpha crossover operator to two parent solutions.
 * @author Antonio J. Nebro.
 * @version 1.0
 */
public class BLXAlphaCrossover implements CrossoverOperator<List<DoubleSolution>, List<DoubleSolution>> {
  private static final double DEFAULT_ALPHA = 0.5;

  private double crossoverProbability;
  private double alpha ;

  private RepairDoubleSolution solutionRepair ;

  private JMetalRandom randomGenerator ;

  /** Constructor */
  public BLXAlphaCrossover(double crossoverProbability, double alpha) {
    this (crossoverProbability, alpha, new RepairDoubleSolutionAtBounds()) ;
  }

  /** Constructor */
  public BLXAlphaCrossover(double crossoverProbability, double alpha, RepairDoubleSolution solutionRepair) {
    if (crossoverProbability < 0) {
      throw new JMetalException("Crossover probability is negative: " + crossoverProbability) ;
    } else if (alpha < 0) {
      throw new JMetalException("Alpha is negative: " + alpha);
    }

    this.crossoverProbability = crossoverProbability ;
    this.alpha = alpha ;
    randomGenerator = JMetalRandom.getInstance() ;
    this.solutionRepair = solutionRepair ;
  }

  /* Getters */
  public double getCrossoverProbability() {
    return crossoverProbability;
  }

  public double getAlpha() {
    return alpha;
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
    double random;
    double valueY1;
    double valueY2;
    double valueX1;
    double valueX2;
    double upperBound;
    double lowerBound;

    if (randomGenerator.nextDouble() <= probability) {
      for (i = 0; i < parent1.getNumberOfVariables(); i++) {
        upperBound = parent1.getUpperBound(i);
        lowerBound = parent1.getLowerBound(i);
        valueX1 = parent1.getVariableValue(i);
        valueX2 = parent2.getVariableValue(i);

        double max;
        double min;
        double range;

        if (valueX2 > valueX1) {
          max = valueX2;
          min = valueX1;
        } else {
          max = valueX1;
          min = valueX2;
        }

        range = max - min;

        double minRange;
        double maxRange;

        minRange = min - range * alpha;
        maxRange = max + range * alpha;

        random = randomGenerator.nextDouble();
        valueY1 = minRange + random * (maxRange - minRange);

        random = randomGenerator.nextDouble();
        valueY2 = minRange + random * (maxRange - minRange);

        valueY1 = solutionRepair.repairSolutionVariableValue(valueY1, lowerBound, upperBound) ;
        valueY2 = solutionRepair.repairSolutionVariableValue(valueY2, lowerBound, upperBound) ;

        offspring.get(0).setVariableValue(i, valueY1);
        offspring.get(1).setVariableValue(i, valueY2);
      }
    }

    return offspring;
  }
}

