package org.uma.jmetal.experimental.auto.parameter;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.checking.Check;

import java.util.function.Function;

public class StringParameter extends Parameter<String> {
  public StringParameter(String name, String[] args) {
    super(name, args) ;
  }

  @Override
  public void check() {
    Check.that(!getName().equals(""), "The parameter name cannot be the empty string");
    Check.isNotNull(getName()) ;
  }

  @Override
  public Parameter<String> parse() {
    return super.parse(Function.identity()) ;
  }
}
