package org.uma.jmetal.algorithm.multiobjective.abyss;

import org.uma.jmetal.algorithm.impl.AbstractScatterSearch;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;

import java.util.List;

/**
 * Created by ajnebro on 11/6/15.
 */
public class AbYSS<S extends Solution> extends AbstractScatterSearch<S, List<S>> {
  protected final int maxEvaluations ;
  protected final Problem<S> problem;
  protected final int referenceSet1Size ;
  protected final int referenceSet2Size ;
  protected final int archiveSize ;

  protected Archive<S> archive ;
  protected LocalSearchOperator<S> localSearch ;
  protected CrossoverOperator<List<S>, List<S>> crossover ;
  protected int evaluations;


  public AbYSS(Problem<S> problem, int maxEvaluations, int populationSize, int referenceSet1Size,
      int referenceSet2Size, int archiveSize, Archive<S> archive, LocalSearchOperator<S> localSearch,
      CrossoverOperator<List<S>, List<S>> crossoverOperator) {
    setPopulationSize(populationSize);
    this.problem = problem ;
    this.maxEvaluations = maxEvaluations ;
    this.referenceSet1Size = referenceSet1Size ;
    this.referenceSet2Size = referenceSet2Size ;
    this.archiveSize = archiveSize ;
    this.archive = archive ;
    this.localSearch = localSearch ;
    this.crossover = crossoverOperator ;

    evaluations = 0 ;
  }

  @Override protected boolean isStoppingConditionReached() {
    return false;
  }

  @Override protected boolean restartConditionIsFulfilled() {
    return false;
  }

  @Override protected void restart() {

  }

  @Override protected S diversificationGeneration() {
    return null;
  }

  @Override protected S improvement(S solution) {
    return null;
  }

  @Override protected void referenceSetUpdate(boolean firstTime) {

  }

  @Override protected List<S> subsetGeneration() {
    return null;
  }

  @Override protected List<S> solutionCombination(List<S> population) {
    return null;
  }

  @Override public List<S> getResult() {
    return null;
  }
}
