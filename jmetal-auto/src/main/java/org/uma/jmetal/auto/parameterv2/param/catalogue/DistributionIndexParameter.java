package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.RealParameter;

public class DistributionIndexParameter extends RealParameter {
  private String name ;
  private String args[] ;

  public DistributionIndexParameter(String name, String args[], Double lowerBound, Double upperBound)  {
    super(name, args, lowerBound, upperBound) ;
    this.args = args ;
    this.name = name ;
  }

  @Override
  public RealParameter parse() {
    setValue(on("--"+name, args, Double::parseDouble));
    return this ;
  }

  @Override
  public String getName() {
    return name;
  }
}
