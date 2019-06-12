package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;

import java.util.Arrays;
import java.util.function.Function;

public class AlgorithmResult extends CategoricalParameter<String> {
  private String args[] ;

  public AlgorithmResult(String args[]) {
    super(Arrays.asList("externalArchive", "population")) ;
    this.args = args ;
  }

  @Override
  public CategoricalParameter<String> parse() {
    value = on("--algorithmResult", args, Function.identity());
    return this ;
  }

  @Override
  public String getName() {
    return "algorithmResult";
  }
}
