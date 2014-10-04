package org.uma.jmetal3.problem;

import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.encoding.IntegerSolution;

/** Interface representing integer problems */
public interface IntegerProblem extends Problem<IntegerSolution> {
  public Integer getLowerBound(int index) ;
  public Integer getUpperBound(int index) ;
}
