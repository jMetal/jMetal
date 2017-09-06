package org.uma.jmetal.solution;

import java.io.Serializable;

/**
 * Interface representing a Solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @param <T> Type (Double, Integer, etc.)
 */
public interface Solution<T> extends Serializable {
  void setObjective(int index, double value) ;
  double getObjective(int index) ;

  T getVariableValue(int index) ;
  void setVariableValue(int index, T value) ;
  String getVariableValueString(int index) ;

  int getNumberOfVariables() ;
  int getNumberOfObjectives() ;

  Solution<T> copy() ;

  void setAttribute(Object id, Object value) ;
  Object getAttribute(Object id) ;
}
