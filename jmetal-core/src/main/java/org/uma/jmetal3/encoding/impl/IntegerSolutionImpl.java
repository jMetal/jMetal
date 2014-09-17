package org.uma.jmetal3.encoding.impl;

import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.IntegerSolution;
import org.uma.jmetal3.encoding.attributes.AlgorithmAttributes;
import org.uma.jmetal3.problem.IntegerProblem;

import java.util.ArrayList;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class IntegerSolutionImpl extends GenericSolutionImpl<Integer, IntegerProblem<IntegerSolution>> implements IntegerSolution {

  /** Constructor */
  public IntegerSolutionImpl(IntegerProblem<IntegerSolution> problem, AlgorithmAttributes attr) {
  	this.problem = problem ;
    objectives = new ArrayList<>(problem.getNumberOfObjectives()) ;
    variables = new ArrayList<>(problem.getNumberOfVariables()) ;
    overallConstraintViolationDegree = 0.0 ;
    attributes = attr ;

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
    attributes = (AlgorithmAttributes)solution.attributes.clone() ;
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
