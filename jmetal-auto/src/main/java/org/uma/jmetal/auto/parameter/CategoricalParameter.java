package org.uma.jmetal.auto.parameter;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public abstract class CategoricalParameter<T> extends Parameter<T> {
  private List<T> validValues;

  public CategoricalParameter(String name, String[] args, List<T> validValues) {
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
    String result =
        "Name: " + getName() + ": " + "Value: " + getValue() + ". Valid values: " + validValues;
    for (Parameter<?> parameter : getGlobalParameters()) {
      result += "\n -> " + parameter.toString();
    }
    for (Pair<String, Parameter<?>> parameter : getSpecificParameters()) {
      result += "\n -> " + parameter.toString();
    }
    return result;
  }
}
