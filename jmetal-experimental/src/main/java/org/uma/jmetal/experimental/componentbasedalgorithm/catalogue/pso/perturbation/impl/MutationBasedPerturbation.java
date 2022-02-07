package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.perturbation.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.perturbation.Perturbation;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.List;
import java.util.stream.IntStream;

/**
 * @author Antonio J. Nebro
 * @author Daniel Doblas
 */

public class MutationBasedPerturbation implements Perturbation {
  private MutationOperator<DoubleSolution> mutationOperator ;
  private double percentageOfApplication ;
  private JMetalRandom randomGenerator ;

  /**
   * Constructor
   * @param mutationOperator : Operator of mutation
   * @param percentageOfApplication: Probability of the mutation
   */
  public MutationBasedPerturbation(MutationOperator<DoubleSolution> mutationOperator, double percentageOfApplication) {
    this.mutationOperator = mutationOperator ;
    randomGenerator = JMetalRandom.getInstance() ;
  }

  /**
   * Constructor
   * @param mutationOperator: Operator of mutation
   */
  public MutationBasedPerturbation(MutationOperator<DoubleSolution> mutationOperator) {
    this(mutationOperator, 0.18) ;
  }

  @Override
  /**
   * @param swarm: List of possible soultions.
   * @return List of mutated possible solutions.
   */
  public List<DoubleSolution> perturb(List<DoubleSolution> swarm) {
    Check.notNull(swarm);
    Check.that(swarm.size() > 0, "The swarm size is empty: " + swarm.size());

    for (DoubleSolution particle : swarm) {
      if (randomGenerator.nextDouble() < percentageOfApplication) {
        mutationOperator.execute(particle) ;
      }
    }
    return swarm;
  }

  /**
   * Operator
   * @return Operator
   */
  public MutationOperator<DoubleSolution> getMutationOperator() {
    return mutationOperator;
  }

  /**
   * Get the percentage of application for mutation operator
   * @return Pertentage of application for mutation operator
   */
  public double getPercentageOfApplication() {
    return percentageOfApplication;
  }
}
