package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.Solution;

/**
 * Created by ajnebro on 26/10/14.
 */
public abstract class AbstractEvolutionStrategy<Sol extends Solution, Result> extends AbstractEvolutionaryAlgorithm<Sol, Result> {
  protected MutationOperator<Sol> mutationOperator ;
}
