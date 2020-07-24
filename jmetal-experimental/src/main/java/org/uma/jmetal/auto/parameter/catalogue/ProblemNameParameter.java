package org.uma.jmetal.auto.parameter.catalogue;

import org.uma.jmetal.auto.parameter.Parameter;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ProblemUtils;

import java.util.function.Function;

public class ProblemNameParameter<S extends Solution<?>> extends Parameter<String> {
  public ProblemNameParameter(String args[]) {
    super("problemName", args) ;
  }

  @Override
  public void check() {
    // nothing to check
  }

  public Problem<S> getProblem() {
    return ProblemUtils.<S>loadProblem(getValue());
  }

  @Override
  public Parameter<String> parse() {
    setValue(on("--problemName", getArgs(), Function.identity())) ;

    return this ;
  }
}
