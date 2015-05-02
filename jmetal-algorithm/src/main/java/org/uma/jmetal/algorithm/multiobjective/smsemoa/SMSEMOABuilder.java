package org.uma.jmetal.algorithm.multiobjective.smsemoa;

import org.uma.jmetal.algorithm.Algorithm;
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
public class SMSEMOABuilder<S extends Solution> implements AlgorithmBuilder {
  private static final double DEFAULT_OFFSET = 100.0 ;

  protected Problem<S> problem;

  protected int populationSize;
  protected int maxEvaluations;

  private CrossoverOperator<List<S>, List<S>> crossoverOperator;
  private MutationOperator<S> mutationOperator;
  protected SelectionOperator selectionOperator;

  protected double offset ;

  public SMSEMOABuilder(Problem<S> problem, CrossoverOperator<List<S>, List<S>> crossoverOperator,
      MutationOperator<S> mutationOperator) {
    this.problem = problem ;
    this.offset = DEFAULT_OFFSET ;
    populationSize = 100 ;
    maxEvaluations = 25000 ;

    this.crossoverOperator = crossoverOperator ;
    this.mutationOperator = mutationOperator ;
    this.selectionOperator = new RandomSelection() ;
  }

  public SMSEMOABuilder setPopulationSize(int populationSize) {
    this.populationSize = populationSize ;

    return this ;
  }

  public SMSEMOABuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations ;

    return this ;
  }

  public SMSEMOABuilder setCrossoverOperator(CrossoverOperator<List<S>, List<S>> crossover) {
    crossoverOperator = crossover ;

    return this ;
  }

  public SMSEMOABuilder setMutationOperator(MutationOperator<S> mutation) {
    mutationOperator = mutation ;

    return this ;
  }

  public SMSEMOABuilder setSelectionOperator(SelectionOperator selection) {
    selectionOperator = selection ;

    return this ;
  }

  public SMSEMOABuilder setOffset(double offset) {
    this.offset = offset ;

    return this ;
  }

  @Override public Algorithm<List<S>> build() {
    return new SMSEMOA<S>(problem, maxEvaluations, populationSize, offset,
        crossoverOperator, mutationOperator, selectionOperator);
  }
}
