package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;

import java.util.HashMap;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class DefaultIntegerSolution extends AbstractGenericSolution<Integer, IntegerProblem> implements IntegerSolution {

  /** Constructor */
  public DefaultIntegerSolution(IntegerProblem problem) {
    super(problem) ;

    overallConstraintViolationDegree = 0.0 ;
    numberOfViolatedConstraints = 0 ;

    for (int i = 0 ; i < problem.getNumberOfVariables(); i++) {
      Integer value = randomGenerator.nextInt(getLowerBound(i), getUpperBound(i));
      setVariableValue(i, value) ;
    }

    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      setObjective(i, 0.0) ;
    }
  }

  /** Copy constructor */
  public DefaultIntegerSolution(DefaultIntegerSolution solution) {
    super(solution.problem) ;

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      setVariableValue(i, solution.getVariableValue(i));
    }

    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      setObjective(i, solution.getObjective(i)) ;
    }

    overallConstraintViolationDegree = solution.overallConstraintViolationDegree ;
    numberOfViolatedConstraints = solution.numberOfViolatedConstraints ;

    attributes = new HashMap<Object, Object>(solution.attributes) ;
  }

  @Override
  public Integer getUpperBound(int index) {
    return problem.getUpperBound(index);
  }

  @Override
  public Integer getLowerBound(int index) {
    return problem.getLowerBound(index) ;
  }

  @Override
  public DefaultIntegerSolution copy() {
    return new DefaultIntegerSolution(this);
  }

  @Override
  public String getVariableValueString(int index) {
    return getVariableValue(index).toString() ;
  }
}
