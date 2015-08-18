package org.uma.jmetal.algorithm.impl;

import java.util.List;

import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.solution.Solution;

/**
 * Created by ajnebro on 26/10/14.
 */
public abstract class AbstractEvolutionStrategy<S extends Solution<?>, Result> extends AbstractEvolutionaryAlgorithm<S, List<S>, Result> {
  protected MutationOperator<S> mutationOperator ;
}
