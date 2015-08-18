package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;

/**
 * Created by ajnebro on 26/10/14.
 */
public abstract class AbstractGeneticAlgorithm<S extends Solution<?>, Population extends Iterable<S>, Result> extends AbstractEvolutionaryAlgorithm<S, Population, Result> {
  protected SelectionOperator<Population, S> selectionOperator ;
  protected CrossoverOperator<S> crossoverOperator ;
  protected MutationOperator<S> mutationOperator ;
}
