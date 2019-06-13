package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.parameterv2.param.IntegerParameter;
import org.uma.jmetal.auto.parameterv2.param.RealParameter;

public class IntegerValueInRange extends IntegerParameter {
  private String name ;
  private String[] args ;

  public IntegerValueInRange(String args[], String name, Integer lowerBound, Integer upperBound)  {
    super(lowerBound, upperBound) ;
    this.name = name ;
    this.args = args ;
  }

  @Override
  public IntegerParameter parse() {
    value = on("--"+name, args, Integer::parseInt);
    return this ;
  }

  @Override
  public String getName() {
    return name;
  }
}
