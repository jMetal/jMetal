package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by ajnebro on 26/10/14.
 */
public abstract class AbstractGeneticAlgorithm2<Sol extends Solution, Result> extends AbstractEvolutionaryAlgorithm2 <Sol, Result> {
  protected SelectionOperator<List<Sol>, Sol> selectionOperator ;
  protected CrossoverOperator<List<Sol>, List<Sol>> crossoverOperator ;
  protected MutationOperator<Sol> mutationOperator ;
}
