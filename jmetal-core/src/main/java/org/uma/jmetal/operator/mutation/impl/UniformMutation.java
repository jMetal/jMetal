package org.uma.jmetal.operator.mutation.impl;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.Check;
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
  private RandomGenerator<Double> randomGenerator;
  private RepairDoubleSolution solutionRepair;

  /** Constructor */
  public UniformMutation(double mutationProbability, double perturbation) {
    this(
        mutationProbability,
        perturbation,
        new RepairDoubleSolutionWithBoundValue(),
        () -> JMetalRandom.getInstance().nextDouble());
  }

  /** Constructor */
  public UniformMutation(
      double mutationProbability,
      double perturbation,
      RepairDoubleSolution solutionRepair,
      RandomGenerator<Double> randomGenerator) {
    this.mutationProbability = mutationProbability;
    this.perturbation = perturbation;
    this.randomGenerator = randomGenerator;
    this.solutionRepair = solutionRepair;
  }

  /** Constructor */
  public UniformMutation(
      double mutationProbability,
      double perturbation,
      RepairDoubleSolution solutionRepair) {
    this(mutationProbability, perturbation, solutionRepair, () -> JMetalRandom.getInstance().nextDouble()) ;
  }


  /* Getters */
  public double getPerturbation() {
    return perturbation;
  }

  @Override
  public double getMutationProbability() {
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
   * @param solution The solution to mutate
   */
  public void doMutation(double probability, DoubleSolution solution) {
    for (var i = 0; i < solution.variables().size(); i++) {
      if (randomGenerator.getRandomValue() < probability) {
        double rand = randomGenerator.getRandomValue();
        var tmp = (rand - 0.5) * perturbation;

        tmp += solution.variables().get(i);

        var bounds = solution.getBounds(i);
        tmp =
            solutionRepair.repairSolutionVariableValue(
                tmp, bounds.getLowerBound(), bounds.getUpperBound());

        solution.variables().set(i, tmp);
      }
    }
  }

  /** Execute() method */
  @Override
  public @NotNull DoubleSolution execute(DoubleSolution solution) {
    Check.notNull(solution);

    doMutation(mutationProbability, solution);

    return solution;
  }
}
