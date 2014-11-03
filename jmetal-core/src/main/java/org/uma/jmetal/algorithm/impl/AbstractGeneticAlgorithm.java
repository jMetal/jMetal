package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by ajnebro on 26/10/14.
 */
public abstract class AbstractGeneticAlgorithm<S extends Solution, Result> extends AbstractEvolutionaryAlgorithm<S, Result> {
  protected SelectionOperator<List<S>, S> selectionOperator ;
  protected CrossoverOperator<List<S>, List<S>> crossoverOperator ;
  protected MutationOperator<S> mutationOperator ;
}
