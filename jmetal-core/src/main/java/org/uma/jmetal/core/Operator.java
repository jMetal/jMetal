package org.uma.jmetal.core;

import org.uma.jmetal45.util.JMetalException;

/** Interface representing an operator */
public interface Operator<Source, Result> {
  /**
   * @param id the ID of the parameter
   * @param value the value of the parameter
   * @throws IllegalArgumentException when the ID is not recognized or the value is not valid for the given ID
   */
  //public <ID, Value> void setParameter(ID id, Value value);

  /**
   * @param source the data to process
   * @throws JMetalException when the combination of parameters is inconsistent (missing or incompatible values)
   */
  public Result execute(Source source) throws JMetalException;
}
