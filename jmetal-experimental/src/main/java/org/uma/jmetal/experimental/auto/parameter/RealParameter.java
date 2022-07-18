package org.uma.jmetal.experimental.auto.parameter;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

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
    return (RealParameter) parse(Double::parseDouble);
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
    var result =
            new StringBuilder("Name: "
                    + getName()
                    + ": "
                    + "Value: "
                    + getValue()
                    + ". Lower bound: "
                    + lowerBound
                    + ". Upper bound: "
                    + upperBound);
    for (var parameter : getGlobalParameters()) {
      result.append("\n -> ").append(parameter.toString());
    }
    for (@NotNull Pair<String, Parameter<?>> parameter : getSpecificParameters()) {
      result.append("\n  -> ").append(parameter.getRight().toString());
    }
    return result.toString();
  }
}
