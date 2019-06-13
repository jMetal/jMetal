package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.Parameter;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ProblemUtils;

import java.util.function.Function;

public class ReferenceFrontFilename extends Parameter<String> {
  private String args[] ;

  public ReferenceFrontFilename(String args[]) {
    this.args = args ;
  }

  @Override
  public void check() {
    // TODO
  }

  @Override
  public Parameter<String> parse() {
    value = on("--referenceFrontFileName", args, Function.identity());

    return this ;
  }

  @Override
  public String getName() {
    return "referenceFrontFileName";
  }
}
