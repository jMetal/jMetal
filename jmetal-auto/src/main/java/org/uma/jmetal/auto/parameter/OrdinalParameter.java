package org.uma.jmetal.auto.parameter;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public abstract class OrdinalParameter<T> extends Parameter<T> {
  private final List<T> validValues;

  public OrdinalParameter(String name, List<T> validValues) {
    super(name);
    this.validValues = validValues;
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

  public List<T> getValidValues() {
    return validValues;
  }

  @Override
  public String toString() {
    StringBuilder result =
            new StringBuilder("Name: " + name() + ": " + "Value: " + value() + ". Valid values: " + validValues);
    for (Parameter<?> parameter : getGlobalParameters()) {
      result.append(" -> ").append(parameter.toString());
    }
    for (Pair<String, Parameter<?>> parameter : getSpecificParameters()) {
      result.append("\n  -> ").append(parameter.getRight().toString());
    }
    return result.toString();
  }
}
