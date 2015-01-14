package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class GenericDoubleSolution extends AbstractGenericSolution<Double, DoubleProblem> implements DoubleSolution {
  /** Constructor */
  public GenericDoubleSolution(DoubleProblem problem) {
    super() ;

  	this.problem = problem ;
    objectives = new ArrayList<>(problem.getNumberOfObjectives()) ;
    variables = new ArrayList<>(problem.getNumberOfVariables()) ;
    overallConstraintViolationDegree = 0.0 ;
    numberOfViolatedConstraints = 0 ;

    for (int i = 0 ; i < problem.getNumberOfVariables(); i++) {
      Double value = randomGenerator.nextDouble() * (getUpperBound(i) - getLowerBound(i)) + getLowerBound(i);
      variables.add(value) ;
    }

    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      objectives.add(new Double(0.0)) ;
    }
  }

  /** Copy constructor */
  public GenericDoubleSolution(GenericDoubleSolution solution) {
    problem = solution.problem ;
    objectives = new ArrayList<>() ;
    for (Double obj : solution.objectives) {
      objectives.add(new Double(obj)) ;
    }
    variables = new ArrayList<>() ;
    for (Double var : solution.variables) {
      variables.add(new Double(var)) ;
    }

    overallConstraintViolationDegree = solution.overallConstraintViolationDegree ;
    numberOfViolatedConstraints = solution.numberOfViolatedConstraints ;
    attributes = new HashMap(solution.attributes) ;
  }

  @Override
  public Double getUpperBound(int index) {
    return problem.getUpperBound(index);
  }

  @Override
  public Double getLowerBound(int index) {
    return problem.getLowerBound(index) ;
  }

  @Override
  public Solution copy() {
    return new GenericDoubleSolution(this);
  }

  @Override
  public String getVariableValueString(int index) {
    return variables.get(index).toString() ;
  }
}
