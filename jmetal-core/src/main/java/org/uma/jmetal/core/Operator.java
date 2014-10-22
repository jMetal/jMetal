package org.uma.jmetal.core;

import org.uma.jmetal45.util.JMetalException;

/**
 * Interface representing an operator
 * @author Antonio J. Nebro
 * @version 0.1
 */
public interface Operator<Source, Result> {
  /**
   * @param source the data to process
   * @throws JMetalException when the combination of parameters is inconsistent (missing or incompatible values)
   */
  public Result execute(Source source) throws JMetalException;
}
