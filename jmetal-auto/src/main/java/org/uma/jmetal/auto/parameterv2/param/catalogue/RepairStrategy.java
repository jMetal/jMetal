package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class RepairStrategy extends CategoricalParameter<String> {
  private String name ;
  private String[] args ;

  public RepairStrategy(String args[], String name, List<String> strategies) {
    super(strategies) ;
    this.name = name ;
    this.args = args ;
  }

  @Override
  public CategoricalParameter<String> parse() {
    value = on("--"+name, args, Function.identity());
    return this ;
  }

  @Override
  public String getName() {
    return name;
  }
}
