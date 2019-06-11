package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Variation extends CategoricalParameter<String> {
  public Variation(String args[]) {
    this(args, Arrays.asList("crossoverAndMutationVariation")) ;
  }

  public Variation(String args[], List<String> variationStrategies) {
    super(variationStrategies) ;
    value = on("--variation", args, Function.identity());
    check(value) ;
  }

  @Override
  public String getName() {
    return "variation";
  }
}
