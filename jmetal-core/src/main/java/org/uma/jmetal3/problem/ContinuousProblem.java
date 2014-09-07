package org.uma.jmetal3.problem;

import org.uma.jmetal3.core.Problem;

/** Interface representing continuous problems */
public interface ContinuousProblem<T extends Number> extends Problem {
  public T getLowerBound(int index) ;
  public T getUpperBound(int index) ;
}
