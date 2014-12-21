package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public abstract class AbstractGenericSolution<T, P extends Problem> implements Solution<T> {
  protected List<Double> objectives;
  protected List<T> variables;
  protected P problem ;
  protected double overallConstraintViolationDegree ;
  protected int numberOfViolatedConstraints ;
  protected Map<Object, Object> attributes ;
  protected final JMetalRandom randomGenerator ;

  /**
   * Constructor
   */
  protected AbstractGenericSolution() {
    attributes = new HashMap<>() ;
    randomGenerator = JMetalRandom.getInstance() ;
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

  @Override public int getNumberOfViolatedConstraints() {
    return numberOfViolatedConstraints;
  }

  @Override
  public void setNumberOfViolatedConstraints(int numberOfViolatedConstraints) {
    this.numberOfViolatedConstraints = numberOfViolatedConstraints;
  }

  protected void initializeObjectiveValues() {
    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      objectives.add(new Double(0.0)) ;
    }
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

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof AbstractGenericSolution)) {
      return false;
    }

    AbstractGenericSolution that = (AbstractGenericSolution) o;

    if (Double.compare(that.overallConstraintViolationDegree, overallConstraintViolationDegree)
        != 0) {
      return false;
    }
    if (attributes != null ? !attributes.equals(that.attributes) : that.attributes != null) {
      return false;
    }
    if (objectives != null ? !objectives.equals(that.objectives) : that.objectives != null) {
      return false;
    }
    //if (problem != null ? !problem.equals(that.problem) : that.problem != null)
    //  return false;
    if (variables != null ? !variables.equals(that.variables) : that.variables != null) {
      return false;
    }

    return true;
  }

  @Override public int hashCode() {
    int result;
    long temp;
    result = objectives != null ? objectives.hashCode() : 0;
    result = 31 * result + (variables != null ? variables.hashCode() : 0);
    //result = 31 * result + (problem != null ? problem.hashCode() : 0);
    temp = Double.doubleToLongBits(overallConstraintViolationDegree);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
    return result;
  }
  //*/
}
