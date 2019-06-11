package org.uma.jmetal.auto.parameterv2.param;

import java.util.Arrays;
import java.util.List;

public abstract class IntegerParameter extends Parameter<Integer> {
  private Integer lowerBound;
  private Integer upperBound;

  public IntegerParameter(Integer lowerBound, Integer upperBound) {
    this.lowerBound = lowerBound ;
    this.upperBound = upperBound ;
  }

  protected void check(Integer value) {
    if ((value < lowerBound) || (value > upperBound))  {
      throw new RuntimeException("Invalid value: " + value + ". Range: " + lowerBound + ", " + upperBound) ;
    }
  }

  public List<Integer> getValidValues() {
    return Arrays.asList(lowerBound, upperBound);
  }

  @Override
  public String toString() {
    String result = "Name: " + getName() + ": " + "Value: " + getValue() + ". Lower bound: " + lowerBound + ". Upper bound: " + upperBound  ;
    for (Parameter<?> parameter : getSpecificParameters()) {
      result += " -> " + parameter.toString() ;
    }
    return result ;
  }
}
