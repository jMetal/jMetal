package org.uma.jmetal.auto.parameter;

import java.util.List;
import java.util.function.Function;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

public class CategoricalParameter extends Parameter<String> {
  private final List<String> validValues;

  public CategoricalParameter(String name, String[] args, List<String> validValues) {
    super(name, args);
    this.validValues = validValues;
  }

  @Override
  public CategoricalParameter parse() {
    return (CategoricalParameter) parse(Function.identity());
  }

  @Override
  public void check() {
    if (!validValues.contains(getValue())) {
      throw new RuntimeException(
          "Parameter "
              + getName()
              + ": Invalid value: "
              + getValue()
              + ". Valid values: "
              + validValues);
    }
  }

  public List<String> getValidValues() {
    return validValues;
  }

  @Override
  public @NotNull String toString() {
    var result =
            new StringBuilder("Name: " + getName() + ": " + "Value: " + getValue() + ". Valid values: " + validValues);
    for (var parameter : getGlobalParameters()) {
      result.append("\n -> ").append(parameter.toString());
    }
    for (@NotNull Pair<String, Parameter<?>> parameter : getSpecificParameters()) {
      result.append("\n -> ").append(parameter.toString());
    }
    return result.toString();
  }
}
