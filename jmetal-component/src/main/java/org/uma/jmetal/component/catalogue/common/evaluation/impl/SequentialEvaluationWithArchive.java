package org.uma.jmetal.component.catalogue.common.evaluation.impl;

import java.util.List;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;

/**
 * @author Antonio J. Nebro (ajnebro@uma.es)
 *
 * @param <S>
 */
public class SequentialEvaluationWithArchive<S extends Solution<?>> extends SequentialEvaluation<S> {
  private Archive<S> archive ;

  public SequentialEvaluationWithArchive(Problem<S> problem, Archive<S> archive) {
    super(problem) ;
    this.archive = archive ;
  }

  @Override
  public List<S> evaluate(List<S> solutionList) {
    List<S> solutions = super.evaluate(solutionList) ;

    solutions.forEach(solution -> archive.add((S)solution.copy()));

    return solutions ;
  }
}
