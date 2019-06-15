package org.uma.jmetal.auto.parameter;

import java.util.Arrays;
import java.util.List;

public class IntegerParameter extends Parameter<Integer> {
  private Integer lowerBound;
  private Integer upperBound;

  public IntegerParameter(String name, String[] args, Integer lowerBound, Integer upperBound) {
    super(name, args) ;
    this.lowerBound = lowerBound ;
    this.upperBound = upperBound ;
  }

  @Override
  public void check() {
    if ((getValue() < lowerBound) || (getValue() > upperBound))  {
      throw new RuntimeException("Invalid value: " + getValue() + ". Range: " + lowerBound + ", " + upperBound) ;
    }
  }

  @Override
  public IntegerParameter parse() {
    setValue(on("--"+getName(), getArgs(), Integer::parseInt));
    return this ;
  }

  public List<Integer> getValidValues() {
    return Arrays.asList(lowerBound, upperBound);
  }

  @Override
  public String toString() {
    String result = "Name: " + getName() + ": " + "Value: " + getValue() + ". Lower bound: " + lowerBound + ". Upper bound: " + upperBound  ;
    for (Parameter<?> parameter : getGlobalParameters()) {
      result += "\n -> " + parameter.toString() ;
    }
    for (Parameter<?> parameter : getSpecificParameters().values()) {
      result += "\n -> " + parameter.toString() ;
    }
    return result ;
  }
}
