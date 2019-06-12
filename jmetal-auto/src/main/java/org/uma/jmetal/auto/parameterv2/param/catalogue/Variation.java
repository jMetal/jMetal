package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;
import org.uma.jmetal.auto.parameterv2.param.Parameter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Variation extends CategoricalParameter<String> {
  private String[] args ;

  public Variation(String args[]) {
    this(args, Arrays.asList("crossoverAndMutationVariation")) ;
  }

  public Variation(String args[], List<String> variationStrategies) {
    super(variationStrategies) ;
    this.args = args ;
  }

  public CategoricalParameter<String> parse() {
    value = on("--variation", args, Function.identity());

    for (Parameter<?> parameter : getGlobalParameters()) {
      parameter.parse().check();
    }

    return this ;
  }

  @Override
  public String getName() {
    return "variation";
  }
}
