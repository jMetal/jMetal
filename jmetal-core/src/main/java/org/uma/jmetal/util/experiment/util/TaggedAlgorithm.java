package org.uma.jmetal.util.experiment.util;

import org.uma.jmetal.algorithm.Algorithm;

/**
 * Created by ajnebro on 10/11/15.
 */
public class TaggedAlgorithm<Result> implements Algorithm<Result> {
  protected Algorithm<Result> algorithm ;

  private String tag ;

  public TaggedAlgorithm (Algorithm<Result> algorithm) {
    this.algorithm = algorithm ;
    tag = algorithm.getName() ;
  }

  public TaggedAlgorithm (Algorithm<Result> algorithm, String tag) {
    this.algorithm = algorithm ;
    this.tag = tag ;
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
}
