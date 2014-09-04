package org.uma.jmetal3.core;

/** Interface representing an operator */
public interface Operator<S, R> {
	  public R execute(S source) ;
}
