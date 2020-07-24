package org.uma.jmetal.auto.parameter;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public class RealParameter extends Parameter<Double> {
  private double lowerBound;
  private double upperBound;

  public RealParameter(String name, String[] args, double lowerBound, double upperBound) {
    super(name, args);
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  @Override
  public RealParameter parse() {
    setValue(on("--" + getName(), getArgs(), Double::parseDouble));
    return this;
  }

  @Override
  public void check() {
    if ((getValue() < lowerBound) || (getValue() > upperBound)) {
      throw new RuntimeException(
          "Parameter "
              + getName()
              + ": Invalid value: "
              + getValue()
              + ". Range: "
              + lowerBound
              + ", "
              + upperBound);
    }
  }

  public List<Double> getValidValues() {
    return Arrays.asList(lowerBound, upperBound);
  }

  @Override
  public String toString() {
    String result =
        "Name: "
            + getName()
            + ": "
            + "Value: "
            + getValue()
            + ". Lower bound: "
            + lowerBound
            + ". Upper bound: "
            + upperBound;
    for (Parameter<?> parameter : getGlobalParameters()) {
      result += "\n -> " + parameter.toString();
    }
    for (Pair<String, Parameter<?>> parameter : getSpecificParameters()) {
      result += "\n  -> " + parameter.getRight().toString();
    }
    return result;
  }
}
