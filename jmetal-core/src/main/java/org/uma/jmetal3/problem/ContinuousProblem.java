package org.uma.jmetal3.problem;

import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.core.Solution;

/** Interface representing continuous problems */
public interface ContinuousProblem<S extends Solution<? extends Double>> extends Problem<S> {
  public Double getLowerBound(int index) ;
  public Double getUpperBound(int index) ;

  @Override
  public S createSolution() ;
}
