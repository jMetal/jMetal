package org.uma.jmetal3.core.impl;

import org.uma.jmetal3.core.NumericalSolution;

/**
 * Created by antonio on 03/09/14.
 */
public class IntRealSolutionImpl<V> implements NumericalSolution {
  double [] objective ;
  V [] variable ;
  int numberOfIntegerVariables ;
  int numberOfRealVariables ;

  /** Constructor */
  public IntRealSolutionImpl(int numberOfObjectives, int numberOfIntegerVariables, int numberOfRealVariables) {
    objective = new double[numberOfObjectives] ;
    this.numberOfIntegerVariables = numberOfIntegerVariables ;
    this.numberOfRealVariables = numberOfRealVariables ;

    variable = (V[])new Object[numberOfIntegerVariables + numberOfRealVariables] ;
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
  public V getVariable(int index) {
    return variable[index];
  }

  @Override
  public void setVariable(int index, Object value) {
    variable[index] = (V)value ;
  }

  @Override
  public Number getUpperBound(int index) {
    return 0.0;
  }
  
  @Override
  public Number getLowerBound(int index) {
    return 0.0;
  }

  public int getNumberOfIntegerVariables() {
    return numberOfIntegerVariables ;
  }

  public int getNumberOfRealVariables() {
    return numberOfRealVariables ;
  }
}
