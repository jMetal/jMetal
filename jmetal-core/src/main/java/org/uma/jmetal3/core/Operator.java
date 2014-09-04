package org.uma.jmetal3.core;

/** Interface representing an operator */
public interface Operator<Source, Result> {
  public Result execute(Source source) ;
}
