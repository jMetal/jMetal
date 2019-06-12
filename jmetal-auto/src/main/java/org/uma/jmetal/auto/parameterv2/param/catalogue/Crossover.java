package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Crossover extends CategoricalParameter<String> {
  public Crossover(String args[]) {
    this(args, Arrays.asList("SBX", "BLX_ALPHA")) ;
  }

  public Crossover(String args[], List<String> crossoverOperators) {
    super(crossoverOperators) ;
    value = on("--crossover", args, Function.identity());
    check(value) ;
  }

  @Override
  public String getName() {
    return "crossover";
  }
}
