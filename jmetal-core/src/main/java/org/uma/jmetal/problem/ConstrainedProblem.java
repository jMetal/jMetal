package org.uma.jmetal.problem;

import org.uma.jmetal.solution.Solution;

import java.io.Serializable;

/**
 * Interface representing a multi-objective optimization problem
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 *
 * @param <S> Encoding
 */
public interface ConstrainedProblem<S> extends Problem<S> {
  enum Attributes{NUMBER_OF_VIOLATED_CONSTRAINTS, OVERALL_CONSTRAINT_VIOLATION_DEGREE}

  void evaluateConstraints(S solution) ;
}
