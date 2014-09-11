package org.uma.jmetal3.encoding.impl;

import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.DoubleSolution;
import org.uma.jmetal3.encoding.IntegerSolution;
import org.uma.jmetal3.problem.ContinuousProblem;
import org.uma.jmetal3.problem.IntegerProblem;
import org.uma.jmetal3.problem.impl.ContinuousProblemImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class DoubleSolutionImpl extends GenericSolutionImpl<Double, ContinuousProblem<DoubleSolution>> implements DoubleSolution {
  /** Constructor */
  public DoubleSolutionImpl(ContinuousProblem<DoubleSolution> problem) {
  	this.problem = problem ;
    objectives = new ArrayList<>(problem.getNumberOfObjectives()) ;
    variables = new ArrayList<>(problem.getNumberOfVariables()) ;
    overallConstraintViolationDegree = 0.0 ;

    for (int i = 0 ; i < problem.getNumberOfVariables(); i++) {
      Double value = PseudoRandom.randDouble() * (getUpperBound(i) - getLowerBound(i)) + getLowerBound(i);
      variables.add(value) ;
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
}
