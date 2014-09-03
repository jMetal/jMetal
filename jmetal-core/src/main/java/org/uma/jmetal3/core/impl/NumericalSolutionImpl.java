package org.uma.jmetal3.core.impl;

import org.uma.jmetal3.core.NumericalSolution;
import org.uma.jmetal3.core.Solution;

/**
 * Created by antonio on 03/09/14.
 */
public class NumericalSolutionImpl<Variable> implements NumericalSolution {
  double [] objective ;
  Variable [] variable ;

  /** Constructor */
  public NumericalSolutionImpl(int numberOfObjectives, int numberOfVariables) {
    objective = new double[numberOfObjectives] ;
    variable = (Variable[])new Object[numberOfVariables] ;
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
  public Variable getVariable(int index) {
    return variable[index];
  }

  @Override
  public void setVariable(int index, Object value) {
    variable[index] = (Variable)value ;
  }

  @Override
  public Object getUpperBound(int index) {
    return null;
  }
}
