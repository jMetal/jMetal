package org.uma.jmetal.util.experiment.util;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

/**
 * Created by ajnebro on 2/12/16.
 */
public class ExperimentProblem<S extends Solution<?>> {
  private Problem<S> problem ;
  private String tag ;

  public ExperimentProblem(Problem<S> problem, String tag) {
    this.problem = problem;
    this.tag = tag;
  }

  public ExperimentProblem(Problem<S> problem) {
    this.problem = problem;
    this.tag = problem.getName() ;
  }

  public Problem<S> getProblem() {
    return problem;
  }

  public String getTag() {
    return tag ;
  }
}
