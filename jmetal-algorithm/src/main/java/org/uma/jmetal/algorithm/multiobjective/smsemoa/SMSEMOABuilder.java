package org.uma.jmetal.algorithm.multiobjective.smsemoa;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.RandomSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;

import java.util.List;

/**
 * Created by ajnebro on 17/4/15.
 */
public class SMSEMOABuilder<S extends Solution<?>> implements AlgorithmBuilder<SMSEMOA<S>> {
  private static final double DEFAULT_OFFSET = 100.0 ;

  protected Problem<S> problem;

  protected int populationSize;
  protected int maxEvaluations;

  private CrossoverOperator<S> crossoverOperator;
  private MutationOperator<S> mutationOperator;
  protected SelectionOperator<List<S>, S> selectionOperator;

  protected double offset ;

  public SMSEMOABuilder(Problem<S> problem, CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator) {
    this.problem = problem ;
    this.offset = DEFAULT_OFFSET ;
    populationSize = 100 ;
    maxEvaluations = 25000 ;

    this.crossoverOperator = crossoverOperator ;
    this.mutationOperator = mutationOperator ;
    this.selectionOperator = new RandomSelection<S>() ;
  }

  public SMSEMOABuilder<S> setPopulationSize(int populationSize) {
    this.populationSize = populationSize ;

    return this ;
  }

  public SMSEMOABuilder<S> setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations ;

    return this ;
  }

  public SMSEMOABuilder<S> setCrossoverOperator(CrossoverOperator<S> crossover) {
    crossoverOperator = crossover ;

    return this ;
  }

  public SMSEMOABuilder<S> setMutationOperator(MutationOperator<S> mutation) {
    mutationOperator = mutation ;

    return this ;
  }

  public SMSEMOABuilder<S> setSelectionOperator(SelectionOperator<List<S>, S> selection) {
    selectionOperator = selection ;

    return this ;
  }

  public SMSEMOABuilder<S> setOffset(double offset) {
    this.offset = offset ;

    return this ;
  }

  @Override public SMSEMOA<S> build() {
    return new SMSEMOA<S>(problem, maxEvaluations, populationSize, offset,
        crossoverOperator, mutationOperator, selectionOperator);
  }
}
