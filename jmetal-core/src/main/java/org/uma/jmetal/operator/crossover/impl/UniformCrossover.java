package org.uma.jmetal.operator.crossover.impl;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements a uniform crosoover operator for binary solutions.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class UniformCrossover implements CrossoverOperator<BinarySolution> {
  private double crossoverProbability;
  private RandomGenerator<Double> crossoverRandomGenerator;

  /** Constructor */
  public UniformCrossover(double crossoverProbability) {
    this(crossoverProbability, () -> JMetalRandom.getInstance().nextDouble());
  }

  /** Constructor */
  public UniformCrossover(
      double crossoverProbability, RandomGenerator<Double> crossoverRandomGenerator) {
    if (crossoverProbability < 0) {
      throw new JMetalException("Crossover probability is negative: " + crossoverProbability);
    }
    this.crossoverProbability = crossoverProbability;
    this.crossoverRandomGenerator = crossoverRandomGenerator;
  }

  /* Getter */
  @Override
  public double getCrossoverProbability() {
    return crossoverProbability;
  }

  /* Setter */
  public void setCrossoverProbability(double crossoverProbability) {
    this.crossoverProbability = crossoverProbability;
  }

  @Override
  public List<BinarySolution> execute(List<BinarySolution> solutions) {
    Check.isNotNull(solutions);
    Check.that(solutions.size() == 2, "There must be two parents instead of " + solutions.size());

    return doCrossover(crossoverProbability, solutions.get(0), solutions.get(1));
  }

  /**
   * Perform the crossover operation.
   *
   * @param probability Crossover setProbability
   * @param parent1 The first parent
   * @param parent2 The second parent
   * @return An array containing the two offspring
   */
  public List<BinarySolution> doCrossover(
      double probability, BinarySolution parent1, BinarySolution parent2) {
    List<BinarySolution> offspring = new ArrayList<>(2);
    offspring.add((BinarySolution) parent1.copy());
    offspring.add((BinarySolution) parent2.copy());

    if (crossoverRandomGenerator.getRandomValue() < probability) {
      for (int variableIndex = 0; variableIndex < parent1.getNumberOfVariables(); variableIndex++) {
        for (int bitIndex = 0;
            bitIndex < parent1.getVariable(variableIndex).getBinarySetLength();
            bitIndex++) {
          if (crossoverRandomGenerator.getRandomValue() < 0.5) {
            offspring
                .get(0)
                .getVariable(variableIndex)
                .set(bitIndex, parent2.getVariable(variableIndex).get(bitIndex));
            offspring
                .get(1)
                .getVariable(variableIndex)
                .set(bitIndex, parent1.getVariable(variableIndex).get(bitIndex));
          }
        }
      }
    }
    return offspring;
  }

  @Override
  public int getNumberOfRequiredParents() {
    return 2;
  }

  @Override
  public int getNumberOfGeneratedChildren() {
    return 2;
  }
}
