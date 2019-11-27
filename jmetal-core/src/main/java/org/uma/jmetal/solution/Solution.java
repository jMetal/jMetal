package org.uma.jmetal.solution;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Interface representing a Solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @param <T> Type (Double, Integer, etc.)
 */
public interface Solution<T> extends Serializable {
  void setObjective(int index, double value) ;
  double getObjective(int index) ;
  double[] getObjectives() ;

  T getVariable(int index) ;
  List<T> getVariables() ;
  void setVariable(int index, T variable) ;

  double[] getConstraints() ;
  double getConstraint(int index) ;
  void setConstraint(int index, double value) ;

  int getNumberOfVariables() ;
  int getNumberOfObjectives() ;
  int getNumberOfConstraints() ;

  Solution<T> copy() ;

  void setAttribute(Object id, Object value) ;
  Object getAttribute(Object id) ;
  boolean hasAttribute(Object id) ;
  
  Map<Object, Object> getAttributes();
}
