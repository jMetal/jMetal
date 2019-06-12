package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;
import org.uma.jmetal.auto.parameterv2.param.IntegerParameter;

import java.util.Arrays;
import java.util.function.Function;

public class CreateInitialSolutions extends CategoricalParameter<String> {
  private String[] args ;

  public CreateInitialSolutions(String args[]) {
    super(Arrays.asList("random", "latinHypercubeSampling", "scatterSearch")) ;
    this.args = args ;
  }

  @Override
  public CategoricalParameter<String>  parse() {
    value = on("--createInitialSolutions", args, Function.identity());
    return this ;
  }

  @Override
  public String getName() {
    return "createInitialSolutions";
  }
}
