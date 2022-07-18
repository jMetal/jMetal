package org.uma.jmetal.operator.mutation.impl;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @version 1.0
 *
 * This class implements a bit flip mutation operator.
 */
@SuppressWarnings("serial")
public class GenericBitFlipMutation<S extends BinarySolution> implements MutationOperator<S> {

  private double mutationProbability;
  private final RandomGenerator<Double> randomGenerator;

  /**
   * Constructor
   */
  public GenericBitFlipMutation(double mutationProbability) {
    this(mutationProbability, () -> JMetalRandom.getInstance().nextDouble());
  }

  /**
   * Constructor
   */
  public GenericBitFlipMutation(double mutationProbability,
      RandomGenerator<Double> randomGenerator) {
    Check.probabilityIsValid(mutationProbability);
    Check.notNull(randomGenerator);

    this.mutationProbability = mutationProbability;
    this.randomGenerator = randomGenerator;
  }

  /* Getter */
  @Override
  public double getMutationProbability() {
    return mutationProbability;
  }

  /* Setters */
  public void setMutationProbability(double mutationProbability) {
    this.mutationProbability = mutationProbability;
  }

  /**
   * Execute() method
   */
  @Override
  public S execute(S solution) {
    Check.notNull(solution);

    doMutation(mutationProbability, solution);
    return solution;
  }

  /**
   * Perform the mutation operation
   *
   * @param probability Mutation setProbability
   * @param solution    The solution to mutate
   */
  public void doMutation(double probability, S solution) {
    for (var i = 0; i < solution.variables().size(); i++) {
      for (var j = 0; j < solution.variables().get(i).getBinarySetLength(); j++) {
        if (randomGenerator.getRandomValue() <= probability) {
          solution.variables().get(i).flip(j);
        }
      }
    }
  }
}
