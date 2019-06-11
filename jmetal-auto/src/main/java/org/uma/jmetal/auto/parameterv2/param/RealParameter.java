package org.uma.jmetal.auto.parameterv2.param;

import java.util.Arrays;
import java.util.List;

public abstract class RealParameter extends Parameter<Double> {
  private double lowerBound;
  private double upperBound;

  public RealParameter(double lowerBound, double upperBound) {
    this.lowerBound = lowerBound ;
    this.upperBound = upperBound ;
  }

  protected void check(Double value) {
    if ((value < lowerBound) || (value > upperBound))  {
      throw new RuntimeException("Invalid value: " + value + ". Range: " + lowerBound + ", " + upperBound) ;
    }
  }

  public List<Double> getValidValues() {
    return Arrays.asList(lowerBound, upperBound);
  }

  @Override
  public String toString() {
    String result = "Name: " + getName() + ": " + "Value: " + getValue() + ". Lower bound: " + lowerBound + ". Upper bound: " + upperBound  ;
    for (Parameter<?> parameter : getGlobalParameters()) {
      result += " -> " + parameter.toString() ;
    }
    for (Parameter<?> parameter : getSpecificParameters()) {
      result += " -> " + parameter.toString() ;
    }
    return result ;
  }
}
