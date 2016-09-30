package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.IntegerDoubleProblem;
import org.uma.jmetal.solution.IntegerDoubleSolution;

import java.util.HashMap;

/**
 * Defines an implementation of a class for solutions having integers and doubles
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultIntegerDoubleSolution
    extends AbstractGenericSolution<Number, IntegerDoubleProblem<?>>
    implements IntegerDoubleSolution {

  private int numberOfIntegerVariables ;
  private int numberOfDoubleVariables ;

  /** Constructor */
  public DefaultIntegerDoubleSolution(IntegerDoubleProblem<?> problem) {
    super(problem) ;

    numberOfIntegerVariables = problem.getNumberOfIntegerVariables() ;
    numberOfDoubleVariables = problem.getNumberOfDoubleVariables() ;

    initializeIntegerDoubleVariables() ;
    initializeObjectiveValues() ;
  }

  /** Copy constructor */
  public DefaultIntegerDoubleSolution(DefaultIntegerDoubleSolution solution) {
    super(solution.problem) ;

    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      setObjective(i, solution.getObjective(i)) ;
    }

    for (int i = 0 ; i < numberOfIntegerVariables; i++) {
      setVariableValue(i, solution.getVariableValue(i)) ;
    }

    for (int i = numberOfIntegerVariables ; i < (numberOfIntegerVariables+numberOfDoubleVariables); i++) {
      setVariableValue(i, solution.getVariableValue(i)) ;
    }

    attributes = new HashMap<Object, Object>(solution.attributes) ;
  }

  @Override
  public Number getUpperBound(int index) {
    return problem.getUpperBound(index);
  }

  @Override
  public int getNumberOfIntegerVariables() {
    return numberOfIntegerVariables;
  }

  @Override
  public int getNumberOfDoubleVariables() {
    return numberOfDoubleVariables;
  }

  @Override
  public Number getLowerBound(int index) {
    return problem.getLowerBound(index) ;
  }

  @Override
  public DefaultIntegerDoubleSolution copy() {
    return new DefaultIntegerDoubleSolution(this);
  }

  @Override
  public String getVariableValueString(int index) {
    return getVariableValue(index).toString() ;
  }
  
  private void initializeIntegerDoubleVariables() {
    for (int i = 0 ; i < numberOfIntegerVariables; i++) {
      Integer value = randomGenerator.nextInt((Integer)getLowerBound(i), (Integer)getUpperBound(i)) ;
      setVariableValue(i, value) ;
    }

    for (int i = numberOfIntegerVariables ; i < getNumberOfVariables(); i++) {
      Double value = randomGenerator.nextDouble((Double)getLowerBound(i), (Double)getUpperBound(i)) ;
      setVariableValue(i, value) ;
    }
  }
}
