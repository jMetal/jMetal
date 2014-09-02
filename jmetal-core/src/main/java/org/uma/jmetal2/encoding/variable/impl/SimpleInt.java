package org.uma.jmetal2.encoding.variable.impl;

import org.uma.jmetal2.core.Variable;
import org.uma.jmetal2.encoding.variable.Int;
import org.uma.jmetal2.encoding.variable.Real;

/**
 * Created by Antonio J. Nebro on 02/09/14.
 */
public class SimpleInt implements Int {
  private Integer value ;

  /** Default constructor */
  public SimpleInt(Integer value) {
    this.value = value ;
  }

  /** Copy constructor */
  public SimpleInt(SimpleInt intVariable) {
    value = intVariable.value ;
  }

  @Override
  public Integer getValue() {
    return value;
  }

  @Override
  public void setValue(Integer value) {
    this.value = value ;
  }

  @Override
  public Variable copy() {
    return new SimpleInt(this);
  }
}
