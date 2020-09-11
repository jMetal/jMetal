package org.uma.jmetal.problem;

import java.io.Serializable;

/**
 * Interface representing a multi-objective optimization problem
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 *
 * @param <S> Encoding
 */
public interface Problem<S> extends Serializable {
  int getNumberOfVariables() ;
  int getNumberOfObjectives() ;
  int getNumberOfConstraints() ;
  String getName() ;

  /**
   * This method receives a solution, evaluates it, and returns the evaluated solution.
   * @param solution
   * @return
   */
  S evaluate(S solution) ;
  S createSolution() ;
}
