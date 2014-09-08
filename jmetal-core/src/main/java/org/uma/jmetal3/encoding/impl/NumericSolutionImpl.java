package org.uma.jmetal3.encoding.impl;

import org.uma.jmetal.metaheuristic.multiobjective.ibea.IBEA;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.NumericSolution;
import org.uma.jmetal3.problem.ContinuousProblem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class NumericSolutionImpl<V extends Number> implements NumericSolution<V> {
  private double [] objectives;
  private List<V> variables;
  private ContinuousProblem problem ;
  private double overallConstraintViolationDegree ;
  
  /** Constructor */
  public NumericSolutionImpl(ContinuousProblem problem) {
  	this.problem = problem ;
    objectives = new double[problem.getNumberOfObjectives()] ;
    variables = new ArrayList<V>(problem.getNumberOfVariables()) ;
    overallConstraintViolationDegree = 0.0 ;

    // NEW CODE
    for (int i = 0 ; i < problem.getNumberOfVariables(); i++) {
      Double value = PseudoRandom.randDouble() * ((Double)getUpperBound(i) - (Double)getLowerBound(i)) + (Double)getLowerBound(i);
      variables.add((V)value) ;
    }
    // END OF NEW CODE
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
  public V getVariableValue(int index) {
    return variables.get(index);
  }

  @Override
  public void setVariableValue(int index, V value) {
    variables.set(index, value) ;
  }

  @Override
  public V getUpperBound(int index) {
    return (V)problem.getUpperBound(index);
  }
  
  @Override
  public V getLowerBound(int index) {
    return (V)problem.getLowerBound(index) ;
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
  public void setOverallConstraintViolationDegree(double violationDegree) {
    overallConstraintViolationDegree = violationDegree ;
  }

  @Override
  public Solution copy() {
    return new NumericSolutionImpl(problem);
  }
}
