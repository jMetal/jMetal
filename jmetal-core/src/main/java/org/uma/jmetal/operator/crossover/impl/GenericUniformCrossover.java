package org.uma.jmetal.operator.crossover.impl;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class implements a uniform crosoover operator for binary solutions.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class GenericUniformCrossover<S extends BinarySolution> implements CrossoverOperator<S> {
  private double crossoverProbability;
    private final RandomGenerator<Double> crossoverRandomGenerator;

  /** Constructor */
  public GenericUniformCrossover(double crossoverProbability) {
    this(crossoverProbability, () -> JMetalRandom.getInstance().nextDouble());
  }

  /** Constructor */
  public GenericUniformCrossover(
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
  public List<S> execute(@NotNull List<S> solutions) {
    Check.notNull(solutions);
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
  public List<S> doCrossover(
      double probability, S parent1, S parent2) {
    List<S> offspring = new ArrayList<>(2);
    offspring.add((S) parent1.copy());
    offspring.add((S) parent2.copy());

    if (crossoverRandomGenerator.getRandomValue() < probability) {
      for (int variableIndex = 0; variableIndex < parent1.variables().size(); variableIndex++) {
        for (int bitIndex = 0;
            bitIndex < parent1.variables().get(variableIndex).getBinarySetLength();
            bitIndex++) {
          if (crossoverRandomGenerator.getRandomValue() < 0.5) {
            offspring
                .get(0)
                .variables().get(variableIndex)
                .set(bitIndex, parent2.variables().get(variableIndex).get(bitIndex));
            offspring
                .get(1)
                .variables().get(variableIndex)
                .set(bitIndex, parent1.variables().get(variableIndex).get(bitIndex));
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
