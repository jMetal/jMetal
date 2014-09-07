package org.uma.jmetal3.problem;

import org.uma.jmetal3.core.Problem;

/** Interface representing continuous problems */
public interface ContinuousProblem extends Problem {
  public Double getLowerBound(int index) ;
  public Double getUpperBound(int index) ;
}
