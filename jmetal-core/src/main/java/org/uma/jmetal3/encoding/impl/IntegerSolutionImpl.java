package org.uma.jmetal3.encoding.impl;

import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.IntegerSolution;
import org.uma.jmetal3.problem.IntegerProblem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class IntegerSolutionImpl extends GenericSolutionImpl<Integer, IntegerProblem<IntegerSolution>> implements IntegerSolution {

  /** Constructor */
  public IntegerSolutionImpl(IntegerProblem<IntegerSolution> problem) {
  	this.problem = problem ;
    objectives = new ArrayList<>(problem.getNumberOfObjectives()) ;
    variables = new ArrayList<>(problem.getNumberOfVariables()) ;
    overallConstraintViolationDegree = 0.0 ;

    for (int i = 0 ; i < problem.getNumberOfVariables(); i++) {
      Integer value = PseudoRandom.randInt(getLowerBound(i), getLowerBound(i));
      variables.add(value) ;
    }
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
    return new IntegerSolutionImpl(problem);
  }
}
