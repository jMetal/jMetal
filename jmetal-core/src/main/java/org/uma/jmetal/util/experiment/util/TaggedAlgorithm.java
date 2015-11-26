package org.uma.jmetal.util.experiment.util;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;

/**
 * This class is a decorator for {@link Algorithm} objects that will be used in an experimental study.
 * An {@link Algorithm} is decorated with two fiels:
 * - problem: the {@link Problem} to be optimized by the {@link Algorithm}
 * - tag: used to indicate the name of the algorithm in the experiment. By the default it is assigned the
 *        value of algorithm.getName(), but it can be set to another value if a same algorithm is configured
 *        with different settings in the experiment.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class TaggedAlgorithm<Result> implements Algorithm<Result> {
  private Algorithm<Result> algorithm ;
  private Problem<?> problem ;
  private String tag ;

  public TaggedAlgorithm (Algorithm<Result> algorithm, Problem<?> problem) {
    this(algorithm, algorithm.getName(), problem) ;
  }

  public TaggedAlgorithm (Algorithm<Result> algorithm, String tag, Problem<?> problem) {
    this.algorithm = algorithm ;
    this.tag = tag ;
    this.problem = problem ;
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
}
