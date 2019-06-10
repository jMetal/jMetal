package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;

import java.util.Arrays;
import java.util.function.Function;

public class CreateInitialSolutions extends CategoricalParameter<String> {
  public CreateInitialSolutions(String args[]) {
    super(Arrays.asList("random", "latinHypercubeSampling", "scatterSearch")) ;
    value = on("--createInitialSolutions", args, Function.identity());
    check(value) ;
  }

  @Override
  public String getName() {
    return "createInitialSolutions";
  }
}
