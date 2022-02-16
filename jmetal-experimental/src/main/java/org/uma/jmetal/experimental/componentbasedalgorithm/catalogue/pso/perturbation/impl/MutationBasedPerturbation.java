package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.perturbation.impl;

import java.util.List;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.perturbation.Perturbation;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * @author Antonio J. Nebro
 * @author Daniel Doblas
 */

public class MutationBasedPerturbation implements Perturbation {
  private MutationOperator<DoubleSolution> mutationOperator ;
  private int frequencyOfApplication ;
  private JMetalRandom randomGenerator ;

  /**
   * Constructor
   * @param mutationOperator : Operator of mutation
   * @param frequencyOfApplication
   */
  public MutationBasedPerturbation(MutationOperator<DoubleSolution> mutationOperator, int frequencyOfApplication) {
    this.mutationOperator = mutationOperator ;
    this.frequencyOfApplication = frequencyOfApplication ;
    randomGenerator = JMetalRandom.getInstance() ;
  }

  /**
   * Constructor
   * @param mutationOperator: Operator of mutation
   */
  public MutationBasedPerturbation(MutationOperator<DoubleSolution> mutationOperator) {
    this(mutationOperator, 7) ;
  }

  @Override
  /**
   * @param swarm: List of possible soultions.
   * @return List of mutated possible solutions.
   */
  public List<DoubleSolution> perturb(List<DoubleSolution> swarm) {
    Check.notNull(swarm);
    Check.that(swarm.size() > 0, "The swarm size is empty: " + swarm.size());

    for (int i = 0; i < swarm.size(); i++) {
      if ((i % frequencyOfApplication) == 0) {
        mutationOperator.execute(swarm.get(i));
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
  public double getFrequencyOfApplication() {
    return frequencyOfApplication;
  }
}
