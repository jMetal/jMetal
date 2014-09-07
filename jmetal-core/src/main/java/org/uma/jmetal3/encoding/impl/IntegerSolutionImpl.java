package org.uma.jmetal3.encoding.impl;

import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.NumericSolution;
import org.uma.jmetal3.problem.ContinuousProblem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class IntegerSolutionImpl implements NumericSolution<Integer> {
  private double [] objectives;
  private List<Integer> variables;
  private ContinuousProblem<Integer> problem ;
  private double overallConstraintViolationDegree ;

  /** Constructor */
  public IntegerSolutionImpl(ContinuousProblem problem) {
  	this.problem = problem ;
    objectives = new double[problem.getNumberOfObjectives()] ;
    variables = new ArrayList<>(problem.getNumberOfVariables()) ;
    overallConstraintViolationDegree = 0.0 ;

    for (int i = 0 ; i < problem.getNumberOfVariables(); i++) {
      Integer value = PseudoRandom.randInt(getLowerBound(i), getLowerBound(i));
      variables.add(value) ;
    }
  }

  @Override
  public void setObjective(int index, double value) {
    objectives[index] = value ;
  }

  @Override
  public double getObjective(int index) {
    return objectives[index];
  }

  @Override
  public Integer getVariableValue(int index) {
    return variables.get(index);
  }

  @Override
  public void setVariableValue(int index, Integer value) {
    variables.set(index, value) ;
  }

  @Override
  public Integer getUpperBound(int index) {
    return (Integer) problem.getUpperBound(index);
  }
  
  @Override
  public Integer getLowerBound(int index) {
    return problem.getLowerBound(index) ;
  }

  @Override
  public int getNumberOfVariables() {
    return variables.size();
  }

  @Override
  public int getNumberOfObjectives() {
    return objectives.length;
  }

  @Override
  public double getOverallConstraintViolationDegree() {
    return overallConstraintViolationDegree ;
  }

  @Override
  public Solution copy() {
    return new IntegerSolutionImpl(problem);
  }
}
