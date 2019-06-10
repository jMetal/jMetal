package org.uma.jmetal.auto.parameterv2.param;

import java.util.List;

public abstract class CategoricalParameter<T> extends Parameter<T> {
  private List<T> validValues ;

  public CategoricalParameter(List<T> validValues) {
    this.validValues = validValues ;
  }

  protected void check(T value) {
    if (!validValues.contains(value)) {
      throw new RuntimeException("Invalid value: " + value + ". Valid values: " + validValues) ;
    }
  }

  public List<T> getValidValues() { return validValues ;}
    /*
    private Parameter<T> parent = null ;
    protected Boolean isGlobalParameter = false ; ;

    protected List<Parameter<?>> globalParameters = new ArrayList<>();
    protected List<Parameter<?>> specificParameters = new ArrayList<>();
    */
}
