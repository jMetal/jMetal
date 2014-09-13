package org.uma.jmetal3.encoding.impl;

import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.BinarySolution;
import org.uma.jmetal3.encoding.attributes.Attributes;
import org.uma.jmetal3.problem.BinaryProblem;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public abstract class GenericSolutionImpl<T, P extends Problem> implements Solution<T> {
  protected List<Double> objectives;
  protected List<T> variables;
  protected P problem ;
  protected double overallConstraintViolationDegree ;
  public Attributes attributes ;

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
  public Attributes getAttributes() {
    return attributes ;
  }

}
