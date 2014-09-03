package org.uma.jmetal3.core2.impl;

import org.uma.jmetal3.core.NumericalSolution;

/**
 * Created by antonio on 03/09/14.
 */
public class IntRealSolutionImpl<V> implements NumericalSolution {
  double [] objective ;
  V [] variable ;

  /** Constructor */
  public IntRealSolutionImpl(int numberOfObjectives, int numberOfVariables) {
    objective = new double[numberOfObjectives] ;
    variable = (V[])new Object[numberOfVariables] ;
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
  public Double getUpperBound(int index) {
    return 0.0;
  }
}
