package org.uma.jmetal.experimental.auto.parameter;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ProblemUtils;

import java.util.function.Function;

public class StringParameter extends Parameter<String> {
  public StringParameter(String name, String[] args) {
    super(name, args) ;
  }

  @Override
  public void check() {
    // nothing to check
  }

  @Override
  public Parameter<String> parse() {
    return super.parse(Function.identity()) ;
  }
}
