package org.uma.jmetal.util.point;

import org.uma.jmetal.solution.Solution;

import java.util.*;

/**
 * This is a simple solution which has only objectives, constraints and properties, but no variables.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class PointSolution implements Solution<Double> {
  private double[] objectives;
  private double[] constraints;
  protected Map<Object, Object> attributes ;

  /**
   * Creates a solution with the given numbers of objectives and constraints,
   * all initialized to zeros.
   *
   * @param numberOfObjectives the number of objectives.
   * @param numberOfConstraints the number of constraints.
   */
  public PointSolution(int numberOfObjectives, int numberOfConstraints) {
    this.objectives = new double[numberOfObjectives];
    this.constraints = new double[numberOfConstraints];
    this.attributes = new HashMap<>() ;
  }

  /**
   * Creates a solution with the given numbers of objectives,
   * all initialized to zeros, and no constraints.
   *
   * @param numberOfObjectives the number of objectives.
   */
  public PointSolution(int numberOfObjectives) {
    this(numberOfObjectives, 0);
  }

  /**
   * Creates a solution, whose objectives are equal to the given point,
   * and no constraints.
   *
   * @param point the point to copy.
   */
  public PointSolution(Point point) {
    this.objectives = point.getValues().clone();
    this.constraints = new double[0];
    this.attributes = new HashMap<>();
  }

  /**
   * Creates a copy of the given solution.
   *
   * @param solution the solution to copy.
   */
  public PointSolution(Solution<?> solution) {
    this.objectives = solution.getObjectives().clone();
    this.constraints = solution.getConstraints().clone();
    this.attributes = new HashMap<>(solution.getAttributes());
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
  public double[] getObjectives() {
    return objectives;
  }

  @Override
  public List<Double> getVariables() {
    return Collections.emptyList() ;
  }

  @Override public Double getVariable(int index) {
    return null;
  }

  @Override public void setVariable(int index, Double value) {
	  //This method is an intentionally-blank override.
  }

  @Override
  public double[] getConstraints() {
    return constraints;
  }

  @Override
  public double getConstraint(int index) {
    return constraints[index];
  }

  @Override
  public void setConstraint(int index, double value) {
    constraints[index] = value;
  }

  @Override public int getNumberOfVariables() {
    return 0;
  }

  @Override public int getNumberOfObjectives() {
    return objectives.length;
  }

  @Override
  public int getNumberOfConstraints() {
    return constraints.length;
  }

  @Override
  public PointSolution copy() {
    return new PointSolution(this);
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
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    PointSolution that = (PointSolution) o;

    return Arrays.equals(objectives, that.objectives)
            && Arrays.equals(constraints, that.constraints)
            && attributes.equals(that.attributes);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(objectives) ^ Arrays.hashCode(constraints) ^ attributes.hashCode();
  }
  
  @Override
  public String toString() {
    return Arrays.toString(objectives);
  }

  @Override
  public Map<Object, Object> getAttributes() {
    return attributes;
  }
}
