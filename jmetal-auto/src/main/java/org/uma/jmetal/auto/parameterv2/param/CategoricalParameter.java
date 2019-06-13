package org.uma.jmetal.auto.parameterv2.param;

import java.util.List;

public abstract class CategoricalParameter<T> extends Parameter<T> {
  private List<T> validValues ;

  public CategoricalParameter(List<T> validValues) {
    this.validValues = validValues ;
  }

  @Override
  public void check() {
    check(value) ;
  }

  protected void check(T value) {
    if (!validValues.contains(value)) {
      throw new RuntimeException("Invalid value: " + value + ". Valid values: " + validValues) ;
    }
  }

  public List<T> getValidValues() { return validValues ;}

  @Override
  public String toString() {
    String result = "Name: " + getName() + ": " + "Value: " + getValue() + ". Valid values: " + validValues ;
    for (Parameter<?> parameter : getGlobalParameters()) {
      result += "\n -> " + parameter.toString() ;
    }
    for (Parameter<?> parameter : getSpecificParameters().values()) {
      result += "\n -> " + parameter.toString() ;
    }
    return result ;
  }
}
