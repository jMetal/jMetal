package org.uma.jmetal.component.catalogue.pso.perturbation.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.component.catalogue.pso.perturbation.Perturbation;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * This perturbation applies a mutation operator to a fixed set of solutions according to a
 * frequency parameter. Given a frequency of application parameter f and a list of solutions,
 * the mutation will be applied to solutions in position p such as p % f == 0.
 *
 * @author Antonio J. Nebro
 * @author Daniel Doblas
 */

public class FrequencySelectionMutationBasedPerturbation implements Perturbation {
  private MutationOperator<DoubleSolution> mutationOperator ;
  private int frequencyOfApplication ;
  private JMetalRandom randomGenerator ;

  /**
   * Constructor
   * @param mutationOperator : Operator of mutation
   * @param frequencyOfApplication
   */
  public FrequencySelectionMutationBasedPerturbation(MutationOperator<DoubleSolution> mutationOperator, int frequencyOfApplication) {
    this.mutationOperator = mutationOperator ;
    this.frequencyOfApplication = frequencyOfApplication ;
    randomGenerator = JMetalRandom.getInstance() ;
  }

  /**
   * Constructor
   * @param mutationOperator: Operator of mutation
   */
  public FrequencySelectionMutationBasedPerturbation(MutationOperator<DoubleSolution> mutationOperator) {
    this(mutationOperator, 7) ;
  }

  @Override
  /**
   * @param swarm: List of possible soultions.
   * @return List of mutated possible solutions.
   */
  public @NotNull List<DoubleSolution> perturb(@NotNull List<DoubleSolution> swarm) {
    Check.notNull(swarm);
    Check.that(swarm.size() > 0, "The swarm size is empty: " + swarm.size());

    for (var i = 0; i < swarm.size(); i++) {
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
