package org.uma.jmetal2.encoding.variable.impl;

import org.uma.jmetal2.core.Variable;
import org.uma.jmetal2.encoding.variable.Real;

/**
 * Created by Antonio J. Nebro on 02/09/14.
 */
public class SimpleReal implements Real {
  private Double value ;

  /** Default constructor */
  public SimpleReal(Double value) {
    this.value = value ;
  }

  /** Copy constructor */
  public SimpleReal(SimpleReal realVariable) {
    value = realVariable.value ;
  }

  @Override
  public Double getValue() {
    return value;
  }

  @Override
  public void setValue(Double value) {
    this.value = value ;
  }

  @Override
  public Variable copy() {
    return new SimpleReal(this);
  }
}
