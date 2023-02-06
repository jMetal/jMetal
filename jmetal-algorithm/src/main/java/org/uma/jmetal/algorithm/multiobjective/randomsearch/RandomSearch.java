package org.uma.jmetal.algorithm.multiobjective.randomsearch;

import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;

/**
 * This class implements a simple random search algorithm.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class RandomSearch<S extends Solution<?>> implements Algorithm<List<S>> {
  private Problem<S> problem ;
  private int maxEvaluations ;
  NonDominatedSolutionListArchive<S> nonDominatedArchive ;

  /** Constructor */
  public RandomSearch(Problem<S> problem, int maxEvaluations) {
    this.problem = problem ;
    this.maxEvaluations = maxEvaluations ;
    nonDominatedArchive = new NonDominatedSolutionListArchive<S>();
  }

  /* Getter */
  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  @Override public void run() {
    for (int i = 0; i < maxEvaluations; i++) {
      S newSolution = problem.createSolution() ;
      problem.evaluate(newSolution);
      nonDominatedArchive.add(newSolution);
    }
  }

  @Override public List<S> result() {
    return nonDominatedArchive.solutions();
  }

  @Override public String name() {
    return "RS" ;
  }

  @Override public String description() {
    return "Multi-objective random search algorithm" ;
  }
} 
