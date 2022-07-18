package org.uma.jmetal.operator.crossover.impl;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class allows to apply a HUX crossover operator using two parent solutions. NOTE: the
 * operator is applied to the first encoding.variable of the solutions, and the type of the
 * solutions must be Binary
 *
 * @author Antonio J. Nebro
 * @author Juan J. Durillo
 * @version 1.0
 */
@SuppressWarnings("serial")
public class GenericHUXCrossover<S extends BinarySolution> implements CrossoverOperator<S> {
  private double crossoverProbability;
    private final RandomGenerator<Double> randomGenerator;

  /** Constructor */
  public GenericHUXCrossover(double crossoverProbability) {
    this(crossoverProbability, () -> JMetalRandom.getInstance().nextDouble());
  }

  /** Constructor */
  public GenericHUXCrossover(double crossoverProbability, RandomGenerator<Double> randomGenerator) {
    Check.probabilityIsValid(crossoverProbability) ;

    this.crossoverProbability = crossoverProbability;
    this.randomGenerator = randomGenerator;
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

  /** Execute() method */
  public List<S> execute(List<S> parents) {
    Check.that(parents.size() == 2, "HUXCrossover.execute: operator needs two parents");

    return doCrossover(crossoverProbability, parents.get(0), parents.get(1));
  }

  /**
   * Perform the crossover operation
   *
   * @param probability Crossover setProbability
   * @param parent1 The first parent
   * @param parent2 The second parent
   * @return An array containing the two offspring
   * @throws JMetalException
   */
  public List<S> doCrossover(
          double probability, S parent1, @NotNull S parent2) throws JMetalException {
    List<S> offspring = new ArrayList<>();
    offspring.add((S) parent1.copy());
    offspring.add((S) parent2.copy());

    if (randomGenerator.getRandomValue() < probability) {
      for (int var = 0; var < parent1.variables().size(); var++) {
        BinarySet p1 = parent1.variables().get(var);
        BinarySet p2 = parent2.variables().get(var);

        for (int bit = 0; bit < p1.size(); bit++) {
          if (p1.get(bit) != p2.get(bit)) {
            if (randomGenerator.getRandomValue() < 0.5) {
              offspring.get(0).variables().get(var).set(bit, p2.get(bit));
              offspring.get(1).variables().get(var).set(bit, p1.get(bit));
            }
          }
        }
      }
    }

    return offspring;
  }

  public int getNumberOfRequiredParents() {
    return 2;
  }

  public int getNumberOfGeneratedChildren() {
    return 2;
  }
}
