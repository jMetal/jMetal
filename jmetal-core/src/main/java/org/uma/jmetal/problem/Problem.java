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
  /* Getters */
  int getNumberOfVariables() ;
  int getNumberOfObjectives() ;
  int getNumberOfConstraints() ;
  String getName() ;

  /* Methods */
  void evaluate(S solution) ;
  S createSolution() ;
}
