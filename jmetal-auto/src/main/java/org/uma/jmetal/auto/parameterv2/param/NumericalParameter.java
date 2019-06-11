package org.uma.jmetal.auto.parameterv2.param;

import java.util.Arrays;
import java.util.List;

public abstract class NumericalParameter<N extends Number> extends Parameter<N> {
  private N lowerBound;
  private N upperBound;

  public NumericalParameter(N lowerBound, N upperBound) {
    this.lowerBound = lowerBound ;
    this.upperBound = upperBound ;
  }

  protected void check(N value) {
    if (value instanceof Integer) {
      if ((value.intValue() < lowerBound.intValue()) || (value.intValue() > upperBound.intValue()))  {
        throw new RuntimeException("Invalid value: " + value + ". Range: " + lowerBound + ", " + upperBound) ;
      }
    } else if (value instanceof Double) {
      if ((value.doubleValue() < lowerBound.doubleValue()) || (value.doubleValue() > upperBound.doubleValue()))  {
        throw new RuntimeException("Invalid value: " + value + ". Range: " + lowerBound + ", " + upperBound) ;
      }
    }
  }

  public List<N> getValidValues() {
    return Arrays.asList(lowerBound, upperBound);
  }
}
