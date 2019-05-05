package org.uma.jmetal.solution;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
  default void setObjectives(double[] objectives) {
    Objects.requireNonNull(objectives, "no objectives provided");
    int numberOfObjectives = getNumberOfObjectives();
    if (objectives.length != numberOfObjectives) {
      throw new IllegalArgumentException(
          String.format("%d objectives expected, but %d received", numberOfObjectives, objectives.length));
    }
    for (int index = 0; index < numberOfObjectives; index++) {
      setObjective(index, objectives[index]);
    }
  }

  T getVariableValue(int index) ;
  List<T> getVariables() ;
  default void setVariables(List<T> variables) {
    Objects.requireNonNull(variables, "no variables provided");
    int numberOfVariables = getNumberOfVariables();
    if (variables.size() != numberOfVariables) {
      throw new IllegalArgumentException(
          String.format("%d variables expected, but %d received", numberOfVariables, variables.size()));
    }
    Iterator<T> iterator = variables.iterator();
    for (int index = 0; index < numberOfVariables; index++) {
      setVariableValue(index, iterator.next());
    }
  }
  void setVariableValue(int index, T value) ;
  String getVariableValueString(int index) ;

  int getNumberOfVariables() ;
  int getNumberOfObjectives() ;

  Solution<T> copy() ;

  void setAttribute(Object id, Object value) ;
  Object getAttribute(Object id) ;
  
  public Map<Object, Object> getAttributes();
}
