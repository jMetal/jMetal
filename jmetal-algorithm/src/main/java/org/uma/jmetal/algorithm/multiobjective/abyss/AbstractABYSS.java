package org.uma.jmetal.algorithm.multiobjective.abyss;

import org.uma.jmetal.algorithm.impl.AbstractScatterSearch;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajnebro on 11/6/15.
 */
public abstract class AbstractABYSS<S extends Solution> extends AbstractScatterSearch<S, List<S>> {
  protected final int maxEvaluations ;
  protected final Problem<S> problem;

  protected final int referenceSet1Size ;
  protected final int referenceSet2Size ;
  protected List<S> referenceSet1 ;
  protected List<S> referenceSet2 ;

  protected final int archiveSize ;
  protected Archive<S> archive ;

  protected LocalSearchOperator<S> localSearch ;
  protected CrossoverOperator<List<S>, List<S>> crossover ;
  protected int evaluations;


  public AbstractABYSS(Problem<S> problem, int maxEvaluations, int populationSize,
      int referenceSet1Size, int referenceSet2Size, int archiveSize, Archive<S> archive,
      LocalSearchOperator<S> localSearch, CrossoverOperator<List<S>, List<S>> crossoverOperator) {
    setPopulationSize(populationSize);
    this.problem = problem ;
    this.maxEvaluations = maxEvaluations ;
    this.referenceSet1Size = referenceSet1Size ;
    this.referenceSet2Size = referenceSet2Size ;
    this.archiveSize = archiveSize ;
    this.archive = archive ;
    this.localSearch = localSearch ;
    this.crossover = crossoverOperator ;

    referenceSet1 = new ArrayList<>(referenceSet1Size) ;
    referenceSet2 = new ArrayList<>(referenceSet2Size) ;

    evaluations = 0 ;
  }

  @Override public boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations ;
  }

  @Override public S improvement(S solution) {
    S improvedSolution = localSearch.execute(solution) ;
    evaluations += localSearch.getEvaluations() ;

    return improvedSolution ;
  }

  @Override public List<S> getResult() {
    return archive.getSolutionList();
  }
}
