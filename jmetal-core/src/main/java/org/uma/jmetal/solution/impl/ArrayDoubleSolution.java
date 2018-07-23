package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link DoubleSolution} using arrays.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class ArrayDoubleSolution implements DoubleSolution {
  private double[] objectives;
  private double[] variables;
  protected DoubleProblem problem ;
  protected Map<Object, Object> attributes ;
  protected final JMetalRandom randomGenerator ;

  /**
   * Constructor
   */
  public ArrayDoubleSolution(DoubleProblem problem) {
    this.problem = problem ;
    attributes = new HashMap<>() ;
    randomGenerator = JMetalRandom.getInstance() ;

    objectives = new double[problem.getNumberOfObjectives()] ;
    variables = new double[problem.getNumberOfVariables()] ;
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      variables[i] = randomGenerator.nextDouble(getLowerBound(i), getUpperBound(i)) ;
    }
  }

  /**
   * Copy constructor
   * @param solution to copy
   */
  public ArrayDoubleSolution(ArrayDoubleSolution solution) {
    this(solution.problem) ;

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      variables[i] = solution.getVariableValue(i) ;
    }

    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      objectives[i] = solution.getObjective(i) ;
    }

    attributes = new HashMap<Object, Object>(solution.attributes) ;
  }

  @Override
  public void setObjective(int index, double value) {
    objectives[index] = value ;
  }

  @Override
  public double getObjective(int index) {
    return objectives[index];
  }

  @Override
  public double[] getObjectives() {
    return objectives ;
  }

  @Override
  public Double getVariableValue(int index) {
    return variables[index];
  }

  @Override
  public void setVariableValue(int index, Double value) {
    variables[index] = value ;
  }

  @Override
  public String getVariableValueString(int index) {
    return getVariableValue(index).toString() ;
  }

  @Override
  public int getNumberOfVariables() {
    return problem.getNumberOfVariables();
  }

  @Override
  public int getNumberOfObjectives() {
    return problem.getNumberOfObjectives();
  }

  @Override
  public Double getUpperBound(int index) {
    return problem.getUpperBound(index);
  }

  @Override
  public Double getLowerBound(int index) {
    return problem.getLowerBound(index) ;
  }

  @Override
  public Solution<Double> copy() {
    return new ArrayDoubleSolution(this);
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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ArrayDoubleSolution that = (ArrayDoubleSolution) o;

    if (!Arrays.equals(objectives, that.objectives)) return false;
    if (!Arrays.equals(variables, that.variables)) return false;
    return problem != null ? problem.equals(that.problem) : that.problem == null;

  }

  @Override
  public int hashCode() {
    int result = Arrays.hashCode(objectives);
    result = 31 * result + Arrays.hashCode(variables);
    result = 31 * result + (problem != null ? problem.hashCode() : 0);
    return result;
  }
}
