package org.uma.jmetal3.problem;

import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.core.Solution;

/** Interface representing integer problems */
public interface IntegerProblem<S extends Solution<? extends Integer>> extends Problem<S> {
  public Integer getLowerBound(int index) ;
  public Integer getUpperBound(int index) ;
}
