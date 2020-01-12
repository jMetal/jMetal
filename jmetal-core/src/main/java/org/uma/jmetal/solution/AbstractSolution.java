package org.uma.jmetal.solution;

import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class representing a generic solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public abstract class AbstractSolution<T> implements Solution<T> {
  private double[] objectives;
  private List<T> variables;
  private double[] constraints;

  protected Map<Object, Object> attributes;

  /** Constructor */
  protected AbstractSolution(int numberOfVariables, int numberOfObjectives) {
    this(numberOfVariables, numberOfObjectives, 0);
  }

  /** Constructor */
  protected AbstractSolution(
      int numberOfVariables, int numberOfObjectives, int numberOfConstraints) {
    attributes = new HashMap<>();

    variables = new ArrayList<>(numberOfVariables);
    for (int i = 0; i < numberOfVariables; i++) {
      variables.add(i, null);
    }

    objectives = new double[numberOfObjectives];
    for (int i = 0; i < numberOfObjectives; i++) {
      objectives[i] = 0.0;
    }

    constraints = new double[numberOfConstraints];
    for (int i = 0; i < numberOfConstraints; i++) {
      constraints[i] = 0.0;
    }

    attributes = new HashMap<Object, Object>();
  }

  @Override
  public double[] getObjectives() {
    return objectives;
  }

  @Override
  public List<T> getVariables() {
    return variables;
  }

  @Override
  public double[] getConstraints() {
    return constraints ;
  }

  @Override
  public void setAttribute(Object id, Object value) {
    attributes.put(id, value);
  }

  @Override
  public Object getAttribute(Object id) {
    return attributes.get(id);
  }

  @Override
  public boolean hasAttribute(Object id) {
    return attributes.containsKey(id);
  }

  @Override
  public void setObjective(int index, double value) {
    objectives[index] = value;
  }

  @Override
  public double getObjective(int index) {
    return objectives[index];
  }

  @Override
  public T getVariable(int index) {
    return variables.get(index);
  }

  @Override
  public void setVariable(int index, T value) {
    variables.set(index, value);
  }

  @Override
  public double getConstraint(int index) {
    return constraints[index] ;
  }

  @Override
  public void setConstraint(int index, double value) {
    constraints[index] = value ;
  }

  @Override
  public int getNumberOfVariables() {
    return variables.size();
  }

  @Override
  public int getNumberOfObjectives() {
    return objectives.length;
  }

  @Override
  public int getNumberOfConstraints() {
    return constraints.length ;
  }

  @Override
  public String toString() {
    String result = "Variables: ";
    for (T var : variables) {
      result += "" + var + " ";
    }
    result += "Objectives: ";
    for (Double obj : objectives) {
      result += "" + obj + " ";
    }
    result += "Constraints: ";
    for (Double obj : constraints) {
      result += "" + obj + " ";
    }
    result += "\t";
    result += "AlgorithmAttributes: " + attributes + "\n";

    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      throw new JMetalException("The solution to compare is null");
    }

    Solution<T> solution = (Solution<T>) o;

    return this.getVariables().equals(solution.getVariables());
  }

  @Override
  public int hashCode() {
    return variables.hashCode();
  }

  @Override
  public Map<Object, Object> getAttributes() {
    return attributes;
  }
}
