package org.uma.jmetal3.encoding.impl;

import org.uma.jmetal3.encoding.NumericSolution;
import org.uma.jmetal3.problem.ContinuousProblem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class NumericSolutionImpl<V extends Number> implements NumericSolution<V> {
  private double [] objective ;
  private List<V> variable ;
  private ContinuousProblem problem ;
  
  /** Constructor */
  public NumericSolutionImpl(int numberOfObjectives, int numberOfVariables) {
    objective = new double[numberOfObjectives] ;
    variable = new ArrayList<V>(numberOfVariables) ;
  }
  
  /** Constructor */
  public NumericSolutionImpl(ContinuousProblem problem) {
    this(problem.getNumberOfObjectives(), problem.getNumberOfVariables()) ;
  	this.problem = problem ;
  }

  @Override
  public void setObjective(int index, double value) {
    objective[index] = value ;
  }

  @Override
  public double getObjective(int index) {
    return objective[index];
  }

  @Override
  public V getVariableValue(int index) {
    return variable.get(index);
  }

  @Override
  public void setVariableVariable(int index, Object value) {
    variable.set(index, (V)value) ;
  }

  @Override
  public V getUpperBound(int index) {
    return (V)problem.getUpperBound(index);
  }
  
  @Override
  public V getLowerBound(int index) {
    return (V)problem.getLowerBound(index) ;
  }
}
