package org.uma.jmetal3.problem;

import org.uma.jmetal3.core.Problem;

public interface NumericProblem extends Problem {
  public Number getUpperBound(int index) ;
  public Number getLowerBound(int index) ;
}
