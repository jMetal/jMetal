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
  @Deprecated
  void setObjective(int index, double value) ;
  @Deprecated
  double getObjective(int index) ;
  @Deprecated
  double[] getObjectives() ;
  default List<Double> objectives() {
    // To be implemented in AbstractSolution
    throw new RuntimeException("Not implemented");
  };
  default double[] objectivesArray() {
    // Just don't care, easier for me that way
    throw new RuntimeException("Not implemented");
  };

  T getVariable(int index) ;
  List<T> getVariables() ;
  void setVariable(int index, T variable) ;

  double[] getConstraints() ;
  double getConstraint(int index) ;
  void setConstraint(int index, double value) ;

  int getNumberOfVariables() ;
  @Deprecated
  int getNumberOfObjectives() ;
  int getNumberOfConstraints() ;

  Solution<T> copy() ;

  void setAttribute(Object id, Object value) ;
  Object getAttribute(Object id) ;
  boolean hasAttribute(Object id) ;
  
  Map<Object, Object> getAttributes();
}
