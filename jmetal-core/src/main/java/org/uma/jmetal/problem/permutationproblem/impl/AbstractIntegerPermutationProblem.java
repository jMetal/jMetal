package org.uma.jmetal.problem.permutationproblem.impl;

import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;

@SuppressWarnings("serial")
public abstract class AbstractIntegerPermutationProblem
    implements PermutationProblem<PermutationSolution<Integer>> {

  protected int numberOfVariables ;
  protected int numberOfObjectives ;
  protected int numberOfConstraints;
  protected String name ;

  @Override
  public int getNumberOfVariables() {
    return numberOfVariables;
  }

  public void setNumberOfVariables(int numberOfVariables) {
    this.numberOfVariables = numberOfVariables ;
  }
  @Override
  public int getNumberOfObjectives() {
    return numberOfObjectives ;
  }

  public void setNumberOfObjectives(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives ;
  }
  public void setNumberOfConstraints(int numberOfConstraints) {
    this.numberOfConstraints = numberOfConstraints ;
  }
  @Override
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  @Override
  public int getNumberOfConstraints() {
    return numberOfConstraints ;
  }

  @Override
  public PermutationSolution<Integer> createSolution() {
    return new IntegerPermutationSolution(getLength(), getNumberOfObjectives()) ;
  }

  @Override
  public int getLength() {
    return numberOfVariables;
  }
}
