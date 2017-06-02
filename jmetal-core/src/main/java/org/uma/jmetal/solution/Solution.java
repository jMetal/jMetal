package org.uma.jmetal.solution;

import java.io.Serializable;

/**
 * Interface representing a Solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @param <T> Type (Double, Integer, etc.)
 */
public interface Solution<T> extends Serializable {
  public void setObjective(int index, double value) ;
  public double getObjective(int index) ;

  public T getVariableValue(int index) ;
  public void setVariableValue(int index, T value) ;
  public String getVariableValueString(int index) ;

  public int getNumberOfVariables() ;
  public int getNumberOfObjectives() ;

  public Solution<T> copy() ;

  public void setAttribute(Object id, Object value) ;
  public Object getAttribute(Object id) ;
}
