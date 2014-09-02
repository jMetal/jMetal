package org.uma.jmetal2.encoding.variable;

import org.uma.jmetal2.core.Variable;

/**
 * Created by Antonio J. Nebro on 02/09/14.
 */
public interface Real extends Variable<Double> {
  public Double getValue() ;
  void setValue(Double value) ;
}
