package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.Parameter;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ProblemUtils;

import java.util.function.Function;

public class ProblemName<S extends Solution<?>> extends Parameter<String> {
  private String args[] ;

  public ProblemName(String args[]) {
    this.args = args ;
  }

  @Override
  public void check() {
    // TODO
  }

  public Problem<S> getProblem() {
    return ProblemUtils.<S>loadProblem(value);
  }

  @Override
  public Parameter<String> parse() {
    value = on("--problemName", args, Function.identity());

    return this ;
  }

  @Override
  public String getName() {
    return "problemName";
  }
}
