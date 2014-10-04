package org.uma.jmetal3.problem;

import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.encoding.DoubleSolution;

/** Interface representing continuous problems */
public interface ContinuousProblem extends Problem<DoubleSolution> {
  public Double getLowerBound(int index) ;
  public Double getUpperBound(int index) ;

  @Override
  public DoubleSolution createSolution() ;
}
