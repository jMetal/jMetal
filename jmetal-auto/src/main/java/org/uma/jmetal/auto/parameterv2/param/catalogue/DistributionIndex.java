package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.RealParameter;

public class DistributionIndex extends RealParameter {
  private String name ;
  private String args[] ;

  public DistributionIndex(String args[], String name, Double lowerBound, Double upperBound)  {
    super(lowerBound, upperBound) ;
    this.args = args ;
    this.name = name ;
  }

  @Override
  public RealParameter parse() {
    value = on("--"+name, args, Double::parseDouble);
    return this ;
  }

  @Override
  public String getName() {
    return name;
  }
}
