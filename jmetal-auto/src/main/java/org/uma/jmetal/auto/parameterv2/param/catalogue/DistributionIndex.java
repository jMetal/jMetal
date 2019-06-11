package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.RealParameter;

public class DistributionIndex extends RealParameter {
  private String name ;

  public DistributionIndex(String args[], String name, Double lowerBound, Double upperBound)  {
    super(lowerBound, upperBound) ;
    this.name = name ;
    value = on("--"+name, args, Double::parseDouble);
    check(value) ;
  }

  @Override
  public String getName() {
    return name;
  }
}
