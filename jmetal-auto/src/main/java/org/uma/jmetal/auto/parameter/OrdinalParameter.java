package org.uma.jmetal.auto.parameter;

import java.util.List;

public abstract class OrdinalParameter<T> extends Parameter<T> {
  private List<T> validValues ;

  public OrdinalParameter(String name, String[] args, List<T> validValues) {
    super(name, args) ;
    this.validValues = validValues ;
  }

  @Override
  public void check() {
    if (!validValues.contains(getValue())) {
      throw new RuntimeException("Invalid value: " + getValue() + ". Valid values: " + validValues) ;
    }  }

  public List<T> getValidValues() { return validValues ;}

  @Override
  public String toString() {
    String result = "Name: " + getName() + ": " + "Value: " + getValue() + ". Valid values: " + validValues ;
    for (Parameter<?> parameter : getGlobalParameters()) {
      result += " -> " + parameter.toString() ;
    }
    for (Parameter<?> parameter : getSpecificParameters().values()) {
      result += " -> " + parameter.toString() ;
    }
    return result ;
  }
}
