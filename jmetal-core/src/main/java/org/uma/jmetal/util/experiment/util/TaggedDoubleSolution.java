package org.uma.jmetal.util.experiment.util;

import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by ajnebro on 23/12/15.
 */
@SuppressWarnings("serial")
public class TaggedDoubleSolution implements TaggedSolution<Double>{
  private String tag ;
  private List<Double> objectives ;
  private List<Double> variables ;

  public TaggedDoubleSolution(String tag, List<Double> variables, List<Double> objectives) {
    this.tag = tag ;
    this.variables = variables ;
    this.objectives = objectives ;
  }

  @Override
  public String getTag() {
    return tag ;
  }

  @Override
  public void setObjective(int index, double value) {

  }

  @Override
  public double getObjective(int index) {
    return objectives.get(index);
  }

  @Override
  public Double getVariableValue(int index) {
    return variables.get(index);
  }

  @Override
  public void setVariableValue(int index, Double value) {

  }

  @Override
  public String getVariableValueString(int index) {
    return null;
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
  public Solution<Double> copy() {
    return null;
  }

  @Override
  public void setAttribute(Object id, Object value) {

  }

  @Override
  public Object getAttribute(Object id) {
    return null;
  }
}
