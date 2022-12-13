package org.uma.jmetal.operator.crossover.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class implements a single point crossover operator.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class SinglePointCrossover<S extends BinarySolution> implements CrossoverOperator<S> {
    private double crossoverProbability;
    private final RandomGenerator<Double> crossoverRandomGenerator;
    private final BoundedRandomGenerator<Integer> pointRandomGenerator;

    /**
     * Constructor
     */
    public SinglePointCrossover(double crossoverProbability) {
        this(
                crossoverProbability,
                () -> JMetalRandom.getInstance().nextDouble(),
                (a, b) -> JMetalRandom.getInstance().nextInt(a, b));
    }

    /**
     * Constructor
     */
  public SinglePointCrossover(
      double crossoverProbability, RandomGenerator<Double> randomGenerator) {
    this(
        crossoverProbability,
        randomGenerator,
        BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
  }

  /** Constructor */
  public SinglePointCrossover(
      double crossoverProbability,
      RandomGenerator<Double> crossoverRandomGenerator,
      BoundedRandomGenerator<Integer> pointRandomGenerator) {
    Check.probabilityIsValid(crossoverProbability);
    this.crossoverProbability = crossoverProbability;
    this.crossoverRandomGenerator = crossoverRandomGenerator;
    this.pointRandomGenerator = pointRandomGenerator;
  }

  /* Getter */
  @Override
  public double crossoverProbability() {
    return crossoverProbability;
  }

  /* Setter */
  public void crossoverProbability(double crossoverProbability) {
    this.crossoverProbability = crossoverProbability;
  }

  @Override
  public List<S> execute(List<S> solutions) {
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
      // 1. Get the total number of bits
      int totalNumberOfBits = parent1.getTotalNumberOfBits();

      // 2. Calculate the point to make the crossover
      int crossoverPoint = pointRandomGenerator.getRandomValue(0, totalNumberOfBits - 1);

      // 3. Compute the variable containing the crossover bit
      int variable = 0;
      int bitsAccount = parent1.variables().get(variable).getBinarySetLength();
      while (bitsAccount < (crossoverPoint + 1)) {
        variable++;
        bitsAccount += parent1.variables().get(variable).getBinarySetLength();
      }

      // 4. Compute the bit into the selected variable
      int diff = bitsAccount - crossoverPoint;
      int intoVariableCrossoverPoint = parent1.variables().get(variable).getBinarySetLength() - diff;

      // 5. Apply the crossover to the variable;
      BinarySet offspring1, offspring2;
      offspring1 = (BinarySet) parent1.variables().get(variable).clone();
      offspring2 = (BinarySet) parent2.variables().get(variable).clone();

      for (int i = intoVariableCrossoverPoint; i < offspring1.getBinarySetLength(); i++) {
        boolean swap = offspring1.get(i);
        offspring1.set(i, offspring2.get(i));
        offspring2.set(i, swap);
      }

      offspring.get(0).variables().set(variable, offspring1);
      offspring.get(1).variables().set(variable, offspring2);

      // 6. Apply the crossover to the other variables
      for (int i = variable + 1; i < parent1.variables().size(); i++) {
        offspring.get(0).variables().set(i, (BinarySet) parent2.variables().get(i).clone());
        offspring.get(1).variables().set(i, (BinarySet) parent1.variables().get(i).clone());
      }
    }
    return offspring;
  }

  @Override
  public int numberOfRequiredParents() {
    return 2;
  }

  @Override
  public int numberOfGeneratedChildren() {
    return 2;
  }
}
