package org.uma.jmetal.operator.impl.mutation;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * This class implements a uniform mutation operator.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class UniformMutation implements MutationOperator<DoubleSolution> {
  private double perturbation;
  private Double mutationProbability = null;
  private RandomGenerator<Double> randomGenenerator ;

  /** Constructor */
  public UniformMutation(double mutationProbability, double perturbation) {
	  this(mutationProbability, perturbation, () -> JMetalRandom.getInstance().nextDouble());
  }

  /** Constructor */
  public UniformMutation(double mutationProbability, double perturbation, RandomGenerator<Double> randomGenenerator) {
    this.mutationProbability = mutationProbability ;
    this.perturbation = perturbation ;
    this.randomGenenerator = randomGenenerator ;
  }

  /* Getters */
  public double getPerturbation() {
    return perturbation;
  }

  public Double getMutationProbability() {
    return mutationProbability;
  }

  /* Setters */
  public void setPerturbation(Double perturbation) {
    this.perturbation = perturbation;
  }

  public void setMutationProbability(Double mutationProbability) {
    this.mutationProbability = mutationProbability;
  }

  /**
   * Perform the operation
   *
   * @param probability Mutation setProbability
   * @param solution    The solution to mutate
   */
  public void doMutation(double probability, DoubleSolution solution)  {
    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      if (randomGenenerator.getRandomValue() < probability) {
        double rand = randomGenenerator.getRandomValue();
        double tmp = (rand - 0.5) * perturbation;

        tmp += solution.getVariableValue(i);

        if (tmp < solution.getLowerBound(i)) {
          tmp = solution.getLowerBound(i);
        } else if (tmp > solution.getUpperBound(i)) {
          tmp = solution.getUpperBound(i);
        }

        solution.setVariableValue(i, tmp);
      }
    }
  }

  /** Execute() method */
  @Override
  public DoubleSolution execute(DoubleSolution solution) {
    if (null == solution) {
      throw new JMetalException("Null parameter");
    }

    doMutation(mutationProbability, solution);

    return solution;
  }
}
