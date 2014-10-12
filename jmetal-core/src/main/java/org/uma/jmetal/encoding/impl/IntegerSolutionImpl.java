package org.uma.jmetal.encoding.impl;

import org.uma.jmetal45.util.random.PseudoRandom;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.encoding.IntegerSolution;
import org.uma.jmetal.problem.IntegerProblem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class IntegerSolutionImpl extends GenericSolutionImpl<Integer, IntegerProblem> implements IntegerSolution {

  /** Constructor */
  public IntegerSolutionImpl(IntegerProblem problem) {
  	this.problem = problem ;
    objectives = new ArrayList<>(problem.getNumberOfObjectives()) ;
    variables = new ArrayList<>(problem.getNumberOfVariables()) ;
    overallConstraintViolationDegree = 0.0 ;

    for (int i = 0 ; i < problem.getNumberOfVariables(); i++) {
      Integer value = PseudoRandom.randInt(getLowerBound(i), getUpperBound(i));
      variables.add(value) ;
    }

    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      objectives.add(new Double(0.0)) ;
    }
  }

  /** Copy constructor */
  public IntegerSolutionImpl(IntegerSolutionImpl solution) {
    problem = solution.problem ;
    objectives = new ArrayList<>() ;
    for (Double obj : solution.objectives) {
      objectives.add(new Double(obj)) ;
    }
    variables = new ArrayList<>() ;
    for (Integer var : solution.variables) {
      variables.add(new Integer(var)) ;
    }

    overallConstraintViolationDegree = solution.overallConstraintViolationDegree ;
    attributes = new HashMap(solution.attributes) ;
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
  public Solution<?> copy() {
    return new IntegerSolutionImpl(this);
  }
}
