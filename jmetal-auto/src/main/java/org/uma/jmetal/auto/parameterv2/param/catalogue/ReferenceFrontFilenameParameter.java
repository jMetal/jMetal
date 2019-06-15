package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.Parameter;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ProblemUtils;

import java.util.function.Function;

public class ReferenceFrontFilenameParameter extends Parameter<String> {
  public ReferenceFrontFilenameParameter(String args[]) {
    super("referenceFrontFileName", args);
  }

  @Override
  public void check() {
    // TODO
  }

  @Override
  public Parameter<String> parse() {
    setValue(on("--referenceFrontFileName", getArgs(), Function.identity()));

    return this;
  }
}
