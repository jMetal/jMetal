package org.uma.jmetal.util.experiment.util;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;

/**
 * Created by ajnebro on 10/11/15.
 */
public class TaggedAlgorithm<Result> implements Algorithm<Result> {
  private Algorithm<Result> algorithm ;
  private Problem<?> problem ;
  private int runId ;

  private String tag ;

  public TaggedAlgorithm (Algorithm<Result> algorithm, Problem<?> problem, int runId) {
    this(algorithm, algorithm.getName(), problem, runId) ;
  }

  public TaggedAlgorithm (Algorithm<Result> algorithm, String tag, Problem<?> problem, int runId) {
    this.algorithm = algorithm ;
    this.tag = tag ;
    this.problem = problem ;
    this.runId = runId ;
  }

  @Override
  public void run() {
    algorithm.run();
  }

  @Override
  public Result getResult() {
    return algorithm.getResult() ;
  }

  @Override
  public String getName() {
    return algorithm.getName();
  }

  @Override
  public String getDescription() {
    return algorithm.getDescription();
  }

  public String getTag() {
    return tag ;
  }

  public void setTag(String tag) {
    this.tag = tag ;
  }

  public Problem<?> getProblem() {
    return problem ;
  }

  public void setProblem(Problem<?> problem) {
    this.problem = problem ;
  }

  public int getRunId() {
    return runId ;
  }
}
