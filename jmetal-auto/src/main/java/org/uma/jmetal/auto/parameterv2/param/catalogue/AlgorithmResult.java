package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;

import java.util.Arrays;
import java.util.function.Function;

public class AlgorithmResult extends CategoricalParameter<String> {
  public AlgorithmResult(String args[]) {
    super(Arrays.asList("externalArchive", "population")) ;
    value = on("--algorithmResult", args, Function.identity());
    check(value) ;
  }

  @Override
  public String getName() {
    return "algorithmResult";
  }
}
