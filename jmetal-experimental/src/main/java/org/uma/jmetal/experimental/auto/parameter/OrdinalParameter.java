package org.uma.jmetal.experimental.auto.parameter;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public abstract class OrdinalParameter<T> extends Parameter<T> {
  private final List<T> validValues;

  public OrdinalParameter(String name, String[] args, List<T> validValues) {
    super(name, args);
    this.validValues = validValues;
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

  public List<T> getValidValues() {
    return validValues;
  }

  @Override
  public String toString() {
    var result =
            new StringBuilder("Name: " + getName() + ": " + "Value: " + getValue() + ". Valid values: " + validValues);
    for (var parameter : getGlobalParameters()) {
      result.append(" -> ").append(parameter.toString());
    }
    for (var parameter : getSpecificParameters()) {
      result.append("\n  -> ").append(parameter.getRight().toString());
    }
    return result.toString();
  }
}
