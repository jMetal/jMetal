package org.uma.jmetal.auto.parameter;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public class CategoricalIntegerParameter extends Parameter<Integer> {
  private final List<Integer> validValues;

  public CategoricalIntegerParameter(String name, List<Integer> validValues) {
    super(name);
    this.validValues = validValues;
  }

  @Override
  public CategoricalIntegerParameter parse(String[] arguments) {
    return (CategoricalIntegerParameter) parse(Integer::parseInt, arguments);
  }

  @Override
  public void check() {
    if (!validValues.contains(value())) {
      throw new RuntimeException(
          "Parameter "
              + name()
              + ": Invalid value: "
              + value()
              + ". Valid values: "
              + validValues);
    }
  }

  public List<Integer> validValues() {
    return validValues;
  }

  @Override
  public String toString() {
    StringBuilder result =
            new StringBuilder("Name: " + name() + ": " + "Value: " + value() + ". Valid values: " + validValues);
    for (Parameter<?> parameter : globalParameters()) {
      result.append("\n -> ").append(parameter.toString());
    }
    for (Pair<String, Parameter<?>> parameter : specificParameters()) {
      result.append("\n -> ").append(parameter.toString());
    }
    return result.toString();
  }
}
