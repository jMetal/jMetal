package org.uma.jmetal.auto.parameter;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class IntegerParameter extends Parameter<Integer> {
  private final Integer lowerBound;
  private final Integer upperBound;

  public IntegerParameter(String name, Integer lowerBound, Integer upperBound) {
    super(name);
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  @Override
  public void check() {
    if ((value() < lowerBound) || (value() > upperBound)) {
      throw new JMetalException(
          "Parameter "
              + name()
              + ": Invalid value: "
              + value()
              + ". Range: "
              + lowerBound
              + ", "
              + upperBound);
    }
  }

  @Override
  public IntegerParameter parse(String[] arguments) {
    return (IntegerParameter) parse(Integer::parseInt, arguments);
  }

  public List<Integer> getValidValues() {
    return Arrays.asList(lowerBound, upperBound);
  }

  @Override
  public String toString() {
    StringBuilder result =
            new StringBuilder("Name: "
                    + name()
                    + ": "
                    + "Value: "
                    + value()
                    + ". Lower bound: "
                    + lowerBound
                    + ". Upper bound: "
                    + upperBound);
    for (Parameter<?> parameter : globalParameters()) {
      result.append("\n -> ").append(parameter.toString());
    }
    for (Pair<String, Parameter<?>> parameter : specificParameters()) {
      result.append("\n  -> ").append(parameter.getRight().toString());
    }
    return result.toString();
  }
}
