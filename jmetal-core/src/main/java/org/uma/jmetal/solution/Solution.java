package org.uma.jmetal.solution;

import static java.util.stream.Collectors.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

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
  default DoubleStream getObjectivesStream() {
    return DoubleStream.of(getObjectives());
  }
  default List<Double> getObjectivesList() {
    return getObjectivesStream().mapToObj(d -> d).collect(toList());
  }

  T getVariable(int index) ;
  List<T> getVariables() ;
  default Stream<T> getVariablesStream() {
    return getVariables().stream();
  }
  void setVariable(int index, T variable) ;

  double[] getConstraints() ;
  default DoubleStream getConstraintsStream() {
    return DoubleStream.of(getConstraints());
  }
  default List<Double> getConstraintsList() {
    return getConstraintsStream().mapToObj(d -> d).collect(toList());
  }
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
