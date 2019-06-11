package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.IntegerParameter;
import org.uma.jmetal.auto.parameterv2.param.RealParameter;

import java.util.function.Function;

public class Probability extends RealParameter {
  private String name ;

  public Probability(String args[], String name)  {
    super(0.0, 1.0) ;
    this.name = name ;
    value = on("--"+name, args, Double::parseDouble);
    check(value) ;
  }

  @Override
  public String getName() {
    return name;
  }
}
