package org.uma.jmetal3.encoding.impl;

import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.encoding.NumericalSolution;
import org.uma.jmetal3.problem.NumericProblem;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class NumericalSolutionImpl<V extends Number> implements NumericalSolution<V> {
  double [] objective ;
  V [] variable ;
  NumericProblem problem ;
  
  /** Constructor */
  public NumericalSolutionImpl(int numberOfObjectives, int numberOfVariables) {
    objective = new double[numberOfObjectives] ;
    variable = (V[])new Object[numberOfVariables] ;
  }
  
  /** Constructor */
  public NumericalSolutionImpl(NumericProblem problem) {
  	this.problem = problem ;
    objective = new double[problem.getNumberOfObjectives()] ;
    variable = (V[])new Object[problem.getNumberOfVariables()] ;
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
    return variable[index];
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
  public void setVariableVariable(int index, V value) {
    variable[index] = value ;  
  }
}
