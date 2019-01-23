package org.uma.jmetal.util.experiment.util;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

/**
 * Class used to add a tag field to a problem.
 *
 * @author Antonio J. Nebro <ajnebro@uma.es>
 */
public class ExperimentProblem<S extends Solution<?>> {
  private Problem<S> problem ;
  private String tag ;
  private String referenceFront;

  public ExperimentProblem(Problem<S> problem, String tag) {
    this.problem = problem;
    this.tag = tag;
    this.referenceFront = this.problem.getName() + ".pf";

  }

  public ExperimentProblem(Problem<S> problem) {
    this(problem,problem.getName());
  }

  public ExperimentProblem<S> changeReferenceFrontTo(String referenceFront) {
    this.referenceFront = referenceFront;
    return this;
  }

  public Problem<S> getProblem() {
    return problem;
  }

  public String getTag() {
    return tag ;
  }

  public String getReferenceFront() {return referenceFront;}
}
