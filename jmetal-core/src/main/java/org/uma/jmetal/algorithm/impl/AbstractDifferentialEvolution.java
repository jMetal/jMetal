package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.solution.DoubleSolution;

/**
 * Created by ajnebro on 26/10/14.
 */
public abstract class AbstractDifferentialEvolution<Result> extends AbstractEvolutionaryAlgorithm<DoubleSolution, Result> {
  protected DifferentialEvolutionCrossover selectionOperator ;
  protected DifferentialEvolutionSelection crossoverOperator ;
}
