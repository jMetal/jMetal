package org.uma.jmetal.operator.impl.crossover;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * This class allows to apply a SBX crossover operator using two parent solutions (Integer encoding)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class IntegerSBXCrossover implements CrossoverOperator<IntegerSolution> {
  /** EPS defines the minimum difference allowed between real values */
  private static final double EPS = 1.0e-14;

  private double distributionIndex ;
  private double crossoverProbability  ;

  private RandomGenerator<Double> randomGenerator ;

  /** Constructor */
  public IntegerSBXCrossover(double crossoverProbability, double distributionIndex) {
	  this(crossoverProbability, distributionIndex, () -> JMetalRandom.getInstance().nextDouble());
  }

  /** Constructor */
  public IntegerSBXCrossover(double crossoverProbability, double distributionIndex, RandomGenerator<Double> randomGenerator) {
    if (crossoverProbability < 0) {
      throw new JMetalException("Crossover probability is negative: " + crossoverProbability) ;
    } else if (distributionIndex < 0) {
      throw new JMetalException("Distribution index is negative: " + distributionIndex);
    }

    this.crossoverProbability = crossoverProbability ;
    this.distributionIndex = distributionIndex ;
    this.randomGenerator = randomGenerator ;
  }

  /* Getters */
  public double getCrossoverProbability() {
    return crossoverProbability;
  }

  public double getDistributionIndex() {
    return distributionIndex;
  }

  /* Setters */
  public void setDistributionIndex(double distributionIndex) {
    this.distributionIndex = distributionIndex;
  }

  public void setCrossoverProbability(double crossoverProbability) {
    this.crossoverProbability = crossoverProbability;
  }

  /** Execute() method */
  @Override
  public List<IntegerSolution> execute(List<IntegerSolution> solutions) {
    if (null == solutions) {
      throw new JMetalException("Null parameter") ;
    } else if (solutions.size() != 2) {
      throw new JMetalException("There must be two parents instead of " + solutions.size()) ;
    }

    return doCrossover(crossoverProbability, solutions.get(0), solutions.get(1)) ;
  }

  /** doCrossover method */
  public List<IntegerSolution> doCrossover(
          double probability, IntegerSolution parent1, IntegerSolution parent2) {
    List<IntegerSolution> offspring = new ArrayList<IntegerSolution>(2);

    offspring.add((IntegerSolution) parent1.copy()) ;
    offspring.add((IntegerSolution) parent2.copy()) ;

    int i;
    double rand;
    double y1, y2, yL, yu;
    double c1, c2;
    double alpha, beta, betaq;
    int valueX1, valueX2;

    if (randomGenerator.getRandomValue() <= probability) {
      for (i = 0; i < parent1.getNumberOfVariables(); i++) {
        valueX1 = parent1.getVariableValue(i);
        valueX2 = parent2.getVariableValue(i);
        if (randomGenerator.getRandomValue() <= 0.5) {
          if (Math.abs(valueX1 - valueX2) > EPS) {

            if (valueX1 < valueX2) {
              y1 = valueX1;
              y2 = valueX2;
            } else {
              y1 = valueX2;
              y2 = valueX1;
            }

            yL = parent1.getLowerBound(i);
            yu = parent1.getUpperBound(i);
            rand = randomGenerator.getRandomValue();
            beta = 1.0 + (2.0 * (y1 - yL) / (y2 - y1));
            alpha = 2.0 - Math.pow(beta, -(distributionIndex + 1.0));

            if (rand <= (1.0 / alpha)) {
              betaq = Math.pow((rand * alpha), (1.0 / (distributionIndex + 1.0)));
            } else {
              betaq = Math
                .pow(1.0 / (2.0 - rand * alpha), 1.0 / (distributionIndex + 1.0));
            }

            c1 = 0.5 * ((y1 + y2) - betaq * (y2 - y1));
            beta = 1.0 + (2.0 * (yu - y2) / (y2 - y1));
            alpha = 2.0 - Math.pow(beta, -(distributionIndex + 1.0));

            if (rand <= (1.0 / alpha)) {
              betaq = Math.pow((rand * alpha), (1.0 / (distributionIndex + 1.0)));
            } else {
              betaq = Math
                .pow(1.0 / (2.0 - rand * alpha), 1.0 / (distributionIndex + 1.0));
            }

            c2 = 0.5 * (y1 + y2 + betaq * (y2 - y1));

            if (c1 < yL) {
              c1 = yL;
            }

            if (c2 < yL) {
              c2 = yL;
            }

            if (c1 > yu) {
              c1 = yu;
            }

            if (c2 > yu) {
              c2 = yu;
            }

            if (randomGenerator.getRandomValue() <= 0.5) {
              offspring.get(0).setVariableValue(i, (int)c2);
              offspring.get(1).setVariableValue(i, (int)c1);
            } else {
              offspring.get(0).setVariableValue(i, (int)c1);
              offspring.get(1).setVariableValue(i, (int)c2);
            }
          } else {
            offspring.get(0).setVariableValue(i, valueX1);
            offspring.get(1).setVariableValue(i, valueX2);
          }
        } else {
          offspring.get(0).setVariableValue(i, valueX2);
          offspring.get(1).setVariableValue(i, valueX1);
        }
      }
    }

    return offspring;
  }

  public int getNumberOfRequiredParents() {
    return 2 ;
  }

  public int getNumberOfGeneratedChildren() {
    return 2 ;
  }
}
