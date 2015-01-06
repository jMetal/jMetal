package org.uma.jmetal.util.point.impl;

import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajnebro on 3/1/15.
 */
public class PointSolution implements Solution<Double> {
  private int numberOfObjectives ;
  protected List<Double> objectives;

  public PointSolution(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives ;
    objectives = new ArrayList<>(numberOfObjectives) ;
  }

  @Override public void setObjective(int index, double value) {
    objectives.set(index, value) ;
  }

  @Override public double getObjective(int index) {
    return objectives.get(index);
  }

  @Override public Double getVariableValue(int index) {
    return null;
  }

  @Override public void setVariableValue(int index, Double value) {

  }

  @Override public String getVariableValueString(int index) {
    return null;
  }

  @Override public int getNumberOfVariables() {
    return 0;
  }

  @Override public int getNumberOfObjectives() {
    return 0;
  }

  @Override public double getOverallConstraintViolationDegree() {
    return 0;
  }

  @Override public void setOverallConstraintViolationDegree(double violationDegree) {

  }

  @Override public int getNumberOfViolatedConstraints() {
    return 0;
  }

  @Override public void setNumberOfViolatedConstraints(int numberOfViolatedConstraints) {

  }

  @Override public Solution copy() {
    return null;
  }

  @Override public void setAttribute(Object id, Object value) {

  }

  @Override public Object getAttribute(Object id) {
    return null;
  }
}
