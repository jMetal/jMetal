package org.uma.jmetal.encoding.impl;

import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public abstract class GenericSolutionImpl<T, P extends Problem> implements Solution<T> {
  protected List<Double> objectives;
  protected List<T> variables;
  protected P problem ;
  protected double overallConstraintViolationDegree ;
  protected Map<Object, Object> attributes ;

  protected GenericSolutionImpl() {
    attributes = new HashMap<>() ;
  }

  @Override
  public void setAttribute(Object id, Object value) {
    attributes.put(id, value) ;
  }

  @Override
  public Object getAttribute(Object id) {
    return attributes.get(id) ;
  }

  @Override
  public void setObjective(int index, double value) {
    objectives.set(index, value) ;
  }

  @Override
  public double getObjective(int index) {
    return objectives.get(index);
  }

  @Override
  public T getVariableValue(int index) {
    return variables.get(index);
  }

  @Override
  public void setVariableValue(int index, T value) {
    variables.set(index, value) ;
  }

  @Override
  public int getNumberOfVariables() {
    return variables.size();
  }

  @Override
  public int getNumberOfObjectives() {
    return objectives.size();
  }

  @Override
  public double getOverallConstraintViolationDegree() {
    return overallConstraintViolationDegree ;
  }

  @Override
  public void setOverallConstraintViolationDegree(double violationDegree) {
    overallConstraintViolationDegree = violationDegree ;
  }

  @Override
  public String toString() {
    String result = "Variables: " ;
    for (T var : variables) {
      result += "" + var + " " ;
    }
    result += "Objectives: " ;
    for (Double obj : objectives) {
      result += "" + obj + " " ;
    }
    result += "\t" ;
    result += "AlgorithmAttributes: " + attributes + "\n" ;

    return result ;
  }
}
