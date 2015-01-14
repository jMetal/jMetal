package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.IntegerDoubleProblem;
import org.uma.jmetal.solution.IntegerDoubleSolution;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class GenericIntegerDoubleSolution
        extends AbstractGenericSolution<Number, IntegerDoubleProblem>
        implements IntegerDoubleSolution {
  private int numberOfIntegerVariables ;
  private int numberOfDoubleVariables ;

  /** Constructor */
  public GenericIntegerDoubleSolution(IntegerDoubleProblem problem) {
  	this.problem = problem ;
    objectives = new ArrayList<>(problem.getNumberOfObjectives()) ;
    variables = new ArrayList<>(problem.getNumberOfVariables()) ;
    numberOfIntegerVariables = problem.getNumberOfIntegerVariables() ;
    numberOfDoubleVariables = problem.getNumberOfDoubleVariables() ;
    overallConstraintViolationDegree = 0.0 ;
    numberOfViolatedConstraints = 0 ;

    for (int i = 0 ; i < numberOfIntegerVariables; i++) {
      Integer value = randomGenerator.nextInt((Integer)getLowerBound(i), (Integer)getUpperBound(i)) ;
      variables.add(value) ;
    }

    for (int i = numberOfIntegerVariables ; i < getNumberOfVariables(); i++) {
      Double value = randomGenerator.nextDouble((Double)getLowerBound(i), (Double)getUpperBound(i)) ;
      variables.add(value) ;
    }

    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      objectives.add(new Double(0.0)) ;
    }
  }

  /** Copy constructor */
  public GenericIntegerDoubleSolution(GenericIntegerDoubleSolution solution) {
    problem = solution.problem ;
    objectives = new ArrayList<>() ;
    for (Double obj : solution.objectives) {
      objectives.add(new Double(obj)) ;
    }
    variables = new ArrayList<>() ;
    for (int i = 0 ; i < numberOfIntegerVariables; i++) {
      variables.add(new Integer((Integer) solution.getVariableValue(i))) ;
    }

    variables = new ArrayList<>() ;
    for (int i = numberOfIntegerVariables ; i < (numberOfIntegerVariables+numberOfDoubleVariables); i++) {
      variables.add(new Double((Double) solution.getVariableValue(i))) ;
    }

    overallConstraintViolationDegree = solution.overallConstraintViolationDegree ;
    numberOfViolatedConstraints = solution.numberOfViolatedConstraints ;

    attributes = new HashMap(solution.attributes) ;
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
  public Solution copy() {
    return new GenericIntegerDoubleSolution(this);
  }

  @Override
  public String getVariableValueString(int index) {
    return variables.get(index).toString() ;
  }
}
