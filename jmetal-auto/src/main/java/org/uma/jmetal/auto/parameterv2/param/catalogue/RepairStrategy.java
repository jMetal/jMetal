package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class RepairStrategy extends CategoricalParameter<String> {
  private String name ;

  public RepairStrategy(String args[], String name, List<String> strategies) {
    super(strategies) ;
    this.name = name ;
    value = on("--"+name, args, Function.identity());
    check(value) ;
  }

  public RepairStrategy(String args[], String name) {
    this(args, name, Arrays.asList("random", "round","bounds")) ;
  }

  @Override
  public String getName() {
    return name;
  }
}
