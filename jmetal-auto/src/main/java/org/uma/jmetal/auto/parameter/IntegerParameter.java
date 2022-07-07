package org.uma.jmetal.auto.parameter;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class IntegerParameter extends Parameter<Integer> {
  private final Integer lowerBound;
  private final Integer upperBound;

  public IntegerParameter(String name, String[] args, Integer lowerBound, Integer upperBound) {
    super(name, args);
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  @Override
  public void check() {
    if ((getValue() < lowerBound) || (getValue() > upperBound)) {
      throw new JMetalException(
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

  @Override
  public IntegerParameter parse() {
    return (IntegerParameter) parse(Integer::parseInt);
  }

  public List<Integer> getValidValues() {
    return Arrays.asList(lowerBound, upperBound);
  }

  @Override
  public String toString() {
    StringBuilder result =
            new StringBuilder("Name: "
                    + getName()
                    + ": "
                    + "Value: "
                    + getValue()
                    + ". Lower bound: "
                    + lowerBound
                    + ". Upper bound: "
                    + upperBound);
    for (Parameter<?> parameter : getGlobalParameters()) {
      result.append("\n -> ").append(parameter.toString());
    }
    for (Pair<String, Parameter<?>> parameter : getSpecificParameters()) {
      result.append("\n  -> ").append(parameter.getRight().toString());
    }
    return result.toString();
  }
}
