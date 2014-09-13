package org.uma.jmetal3.encoding.impl;

import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.IntegerSolution;
import org.uma.jmetal3.encoding.attributes.Attributes;
import org.uma.jmetal3.problem.IntegerProblem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class IntegerSolutionImpl extends GenericSolutionImpl<Integer, IntegerProblem<IntegerSolution>> implements IntegerSolution {

  /** Constructor */
  public IntegerSolutionImpl(IntegerProblem<IntegerSolution> problem, Attributes attr) {
  	this.problem = problem ;
    objectives = new ArrayList<>(problem.getNumberOfObjectives()) ;
    variables = new ArrayList<>(problem.getNumberOfVariables()) ;
    overallConstraintViolationDegree = 0.0 ;

    for (int i = 0 ; i < problem.getNumberOfVariables(); i++) {
      Integer value = PseudoRandom.randInt(getLowerBound(i), getLowerBound(i));
      variables.add(value) ;
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
