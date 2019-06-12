package org.uma.jmetal.auto.parameterv2.param.catalogue;

import afu.org.checkerframework.checker.oigj.qual.O;
import org.uma.jmetal.auto.parameterv2.param.IntegerParameter;
import org.uma.jmetal.auto.parameterv2.param.Parameter;

public class PopulationSize extends Parameter<Integer> {
  private String args[] ;

  public PopulationSize(String args[]) {
    this.args = args ;
  }

  @Override
  public Parameter<Integer> parse() {
    value = on("--populationSize", args, Integer::parseInt);
    return this ;
  }

  @Override
  public void check() {
    if (value <= 0) {
      throw new RuntimeException("The population size cannot not be <= 0") ;
    }
  }

  @Override
  public String getName() {
    return "populationSize";
  }
}
