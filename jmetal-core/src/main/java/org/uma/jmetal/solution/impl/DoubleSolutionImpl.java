package org.uma.jmetal.solution.impl;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.problem.ContinuousProblem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class DoubleSolutionImpl extends GenericSolutionImpl<Double, ContinuousProblem> implements DoubleSolution {
  /** Constructor */
  public DoubleSolutionImpl(ContinuousProblem problem) {
    super() ;

  	this.problem = problem ;
    objectives = new ArrayList<>(problem.getNumberOfObjectives()) ;
    variables = new ArrayList<>(problem.getNumberOfVariables()) ;
    overallConstraintViolationDegree = 0.0 ;

    for (int i = 0 ; i < problem.getNumberOfVariables(); i++) {
      Double value = randomGenerator.nextDouble() * (getUpperBound(i) - getLowerBound(i)) + getLowerBound(i);
      variables.add(value) ;
    }

    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      objectives.add(new Double(0.0)) ;
    }
  }

  /** Copy constructor */
  public DoubleSolutionImpl(DoubleSolutionImpl solution) {
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
  public Solution<?> copy() {
    return new DoubleSolutionImpl(this);
  }

//  @Override
//  public String toString() {
//    String result = "Variables: " ;
//    for (Double var : variables) {
//      result += "" + var + " " ;
//    }
//    result += "Objectives: " ;
//    for (Double obj : objectives) {
//      result += "" + obj + " " ;
//    }
//    result += "\n" ;
//    result += "AlgorithmAttributes: " + attributes ;
//
//    return result ;
//  }
}
