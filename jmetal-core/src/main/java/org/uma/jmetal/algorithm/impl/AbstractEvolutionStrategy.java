package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

/**
 * Created by ajnebro on 26/10/14.
 */
public abstract class AbstractEvolutionStrategy<S extends Solution<?>, Result> extends AbstractEvolutionaryAlgorithm<S, Result> {
  protected MutationOperator<S> mutationOperator ;

  /* Getter */
  public MutationOperator<S> getMutationOperator() {
    return mutationOperator;
  }

  /**
   * Constructor
   * @param problem The problem to solve
   */
  public AbstractEvolutionStrategy(Problem<S> problem) {
    setProblem(problem);
  }
}
