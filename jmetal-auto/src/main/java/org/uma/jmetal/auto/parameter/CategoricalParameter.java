package org.uma.jmetal.auto.parameter;

import java.util.List;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.Pair;

public class CategoricalParameter extends Parameter<String> {
  private final List<String> validValues;

  public CategoricalParameter(String name, List<String> validValues) {
    super(name);
    this.validValues = validValues;
  }

  @Override
  public CategoricalParameter parse(String[] arguments) {
    return (CategoricalParameter) parse(Function.identity(), arguments);
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

  public List<String> validValues() {
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
