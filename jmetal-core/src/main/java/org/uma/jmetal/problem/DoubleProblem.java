package org.uma.jmetal.problem;

import org.uma.jmetal.solution.DoubleSolution;

/** Interface representing continuous problems */
public interface DoubleProblem extends Problem<DoubleSolution> {
  public Double getLowerBound(int index) ;
  public Double getUpperBound(int index) ;

  @Override
  public DoubleSolution createSolution() ;
}
