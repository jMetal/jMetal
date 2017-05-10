package org.uma.jmetal.problem.impl;

import org.uma.jmetal.problem.Problem;

@SuppressWarnings("serial")
public abstract class AbstractGenericProblem<S> implements Problem<S> {
  private int numberOfVariables = 0 ;
  private int numberOfObjectives = 0 ;
  private int numberOfConstraints = 0 ;
  private String name = null ;

  /* Getters */
  @Override
  public int getNumberOfVariables() {
    return numberOfVariables ;
  }

  @Override
  public int getNumberOfObjectives() {
    return numberOfObjectives ;
  }

  @Override
  public int getNumberOfConstraints() {
    return numberOfConstraints ;
  }

  @Override
  public String getName() {
    return name ;
  }

  /* Setters */
  protected void setNumberOfVariables(int numberOfVariables) {
    this.numberOfVariables = numberOfVariables;
  }

  protected void setNumberOfObjectives(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives;
  }

  protected void setNumberOfConstraints(int numberOfConstraints) {
    this.numberOfConstraints = numberOfConstraints;
  }

  protected void setName(String name) {
    this.name = name;
  }
}
