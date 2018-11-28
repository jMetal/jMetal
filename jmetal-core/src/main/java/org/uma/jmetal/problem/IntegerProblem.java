package org.uma.jmetal.problem;

import org.uma.jmetal.solution.IntegerSolution;

/**
 * Interface representing integer problems
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface IntegerProblem extends Problem<IntegerSolution> {
  public Integer getLowerBound(int index) ;
  public Integer getUpperBound(int index) ;
}
