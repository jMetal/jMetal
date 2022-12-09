package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.operator.mutation.MutationOperator;
/**
 * Abstract class representing an evolution strategy algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

@SuppressWarnings("serial")
public abstract class AbstractEvolutionStrategy<S, Result> extends AbstractEvolutionaryAlgorithm<S, Result> {
  protected MutationOperator<S> mutationOperator ;

  /* Getter */
  public MutationOperator<S> getMutationOperator() {
    return mutationOperator;
  }
}
