package org.uma.jmetal.operator.impl.mutation;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class implements a non-uniform mutation operator.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class NonUniformMutation implements MutationOperator<DoubleSolution> {
  private double perturbation;
  private int maxIterations;
  private double mutationProbability;

  private int currentIteration;
  private RandomGenerator<Double> randomGenenerator ;

  /** Constructor */
  public NonUniformMutation(double mutationProbability, double perturbation, int maxIterations) {
	  this(mutationProbability, perturbation, maxIterations, () -> JMetalRandom.getInstance().nextDouble());
  }

  /** Constructor */
  public NonUniformMutation(double mutationProbability, double perturbation, int maxIterations, RandomGenerator<Double> randomGenenerator) {
    this.perturbation = perturbation ;
    this.mutationProbability = mutationProbability ;
    this.maxIterations = maxIterations ;

    this.randomGenenerator = randomGenenerator ;
  }

  /* Getters */
  public double getPerturbation() {
    return perturbation;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public double getMutationProbability() {
    return mutationProbability;
  }

  public int getCurrentIteration() {
    return currentIteration;
  }

  /* Setters */
  public void setCurrentIteration(int currentIteration) {
    if (currentIteration < 0) {
      throw new JMetalException("Iteration number cannot be a negative value: " + currentIteration) ;
    }

    this.currentIteration = currentIteration;
  }

  public void setPerturbation(double perturbation) {
    this.perturbation = perturbation;
  }

  public void setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;
  }

  public void setMutationProbability(double mutationProbability) {
    this.mutationProbability = mutationProbability;
  }

  /** Execute() method */
  @Override
  public DoubleSolution execute(DoubleSolution solution) {
    if (null == solution) {
      throw new JMetalException("Null parameter") ;
    }

    doMutation(mutationProbability, solution);

    return solution;
  }

  /**
   * Perform the mutation operation
   *
   * @param probability Mutation setProbability
   * @param solution    The solution to mutate
   */
  public void doMutation(double probability, DoubleSolution solution){
    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      if (randomGenenerator.getRandomValue() < probability) {
        double rand = randomGenenerator.getRandomValue();
        double tmp;

        if (rand <= 0.5) {
          tmp = delta(solution.getUpperBound(i) - solution.getVariableValue(i),
              perturbation);
          tmp += solution.getVariableValue(i);
        } else {
          tmp = delta(solution.getLowerBound(i) - solution.getVariableValue(i),
              perturbation);
          tmp += solution.getVariableValue(i);
        }

        if (tmp < solution.getLowerBound(i)) {
          tmp = solution.getLowerBound(i);
        } else if (tmp > solution.getUpperBound(i)) {
          tmp = solution.getUpperBound(i);
        }
        solution.setVariableValue(i, tmp);
      }
    }
  }


  /** Calculates the delta value used in NonUniform mutation operator */
  private double delta(double y, double bMutationParameter) {
    double rand = randomGenenerator.getRandomValue();
    int it, maxIt;
    it = currentIteration;
    maxIt = maxIterations;

    return (y * (1.0 -
        Math.pow(rand,
            Math.pow((1.0 - it / (double) maxIt), bMutationParameter)
        )));
  }
}
