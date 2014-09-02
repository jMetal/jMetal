package org.uma.jmetal2.encoding.variable;

import org.uma.jmetal2.core.Variable;

/**
 * Created by Antonio J. Nebro on 02/09/14.
 */
public interface Int extends Variable<Integer> {
  public Integer getValue() ;
  void setValue(Integer value) ;
}
