package org.uma.jmetal.problem.permutationproblem.impl;

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
  public int numberOfVariables() {
    return numberOfVariables;
  }

  public void setNumberOfVariables(int numberOfVariables) {
    this.numberOfVariables = numberOfVariables ;
  }
  @Override
  public int numberOfObjectives() {
    return numberOfObjectives ;
  }

  public void setNumberOfObjectives(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives ;
  }
  public void setNumberOfConstraints(int numberOfConstraints) {
    this.numberOfConstraints = numberOfConstraints ;
  }
  @Override
  public String name() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  @Override
  public int numberOfConstraints() {
    return numberOfConstraints ;
  }

  @Override
  public PermutationSolution<Integer> createSolution() {
    return new IntegerPermutationSolution(length(), numberOfObjectives()) ;
  }

  @Override
  public int length() {
    return numberOfVariables();
  }
}
