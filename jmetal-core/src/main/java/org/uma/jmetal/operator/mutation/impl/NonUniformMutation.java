package org.uma.jmetal.operator.mutation.impl;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.Check;
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
  private RepairDoubleSolution solutionRepair;

  /** Constructor */
  public NonUniformMutation(double mutationProbability, double perturbation, int maxIterations) {
	  this(mutationProbability, perturbation, maxIterations, new RepairDoubleSolutionWithBoundValue(), () -> JMetalRandom.getInstance().nextDouble());
  }

  /** Constructor */
  public NonUniformMutation(double mutationProbability, double perturbation, int maxIterations, RepairDoubleSolution solutionRepair) {
    this(mutationProbability, perturbation, maxIterations, solutionRepair, () -> JMetalRandom.getInstance().nextDouble());
  }

  /** Constructor */
  public NonUniformMutation(double mutationProbability, double perturbation, int maxIterations, RepairDoubleSolution solutionRepair, RandomGenerator<Double> randomGenenerator) {
    this.perturbation = perturbation ;
    this.mutationProbability = mutationProbability ;
    this.maxIterations = maxIterations ;
    this.solutionRepair = solutionRepair ;

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
    Check.that(currentIteration >= 0, "The iteration number cannot be a negative value: " + currentIteration);

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
  public void doMutation(double probability, DoubleSolution solution){
    for (int i = 0; i < solution.variables().size(); i++) {
      if (randomGenenerator.getRandomValue() < probability) {
        double rand = randomGenenerator.getRandomValue();
        double tmp;

        Bounds<Double> bounds = solution.getBounds(i);
        if (rand <= 0.5) {
          tmp = delta(bounds.getUpperBound() - solution.variables().get(i),
              perturbation);
          tmp += solution.variables().get(i);
        } else {
          tmp = delta(bounds.getLowerBound() - solution.variables().get(i),
              perturbation);
          tmp += solution.variables().get(i);
        }

        tmp = solutionRepair.repairSolutionVariableValue(
                        tmp, bounds.getLowerBound(), bounds.getUpperBound());

        solution.variables().set(i, tmp);
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
