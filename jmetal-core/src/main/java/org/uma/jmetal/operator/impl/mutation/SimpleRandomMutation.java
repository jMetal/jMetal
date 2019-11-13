package org.uma.jmetal.operator.impl.mutation;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class implements a random mutation operator for double solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class SimpleRandomMutation implements MutationOperator<DoubleSolution> {
  private double mutationProbability ;
  private RandomGenerator<Double> randomGenerator ;

  /**  Constructor */
  public SimpleRandomMutation(double probability) {
	  this(probability, () -> JMetalRandom.getInstance().nextDouble());
  }

  /**  Constructor */
  public SimpleRandomMutation(double probability, RandomGenerator<Double> randomGenerator) {
    if (probability < 0) {
      throw new JMetalException("Mutation probability is negative: " + mutationProbability) ;
    }

  	this.mutationProbability = probability ;
    this.randomGenerator = randomGenerator ;
  }

  /* Getters */
  public double getMutationProbability() {
    return mutationProbability;
  }

  /* Setters */
  public void setMutationProbability(double mutationProbability) {
    this.mutationProbability = mutationProbability;
  }

  /** Execute() method */
	@Override
  public DoubleSolution execute(DoubleSolution solution) throws JMetalException {
    if (null == solution) {
      throw new JMetalException("Null parameter") ;
    }

    doMutation(mutationProbability, solution) ;
    
    return solution;
  }

  /** Implements the mutation operation */
	private void doMutation(double probability, DoubleSolution solution) {
    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      if (randomGenerator.getRandomValue() <= probability) {
      	Double value = solution.getLowerBound(i) +
      			((solution.getUpperBound(i) - solution.getLowerBound(i)) * randomGenerator.getRandomValue()) ;
      	
      	solution.setVariableValue(i, value) ;
      }
    }
	}
}
