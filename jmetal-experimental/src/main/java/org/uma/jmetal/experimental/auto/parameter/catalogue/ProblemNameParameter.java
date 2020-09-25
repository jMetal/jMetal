package org.uma.jmetal.experimental.auto.parameter.catalogue;

import org.uma.jmetal.experimental.auto.parameter.Parameter;
import org.uma.jmetal.experimental.auto.parameter.StringParameter;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ProblemUtils;

import java.util.function.Function;

public class ProblemNameParameter<S extends Solution<?>> extends StringParameter {
  public ProblemNameParameter(String[] args) {
    super("problemName", args) ;
  }

  public Problem<S> getProblem() {
    return ProblemUtils.loadProblem(getValue());
  }
}
