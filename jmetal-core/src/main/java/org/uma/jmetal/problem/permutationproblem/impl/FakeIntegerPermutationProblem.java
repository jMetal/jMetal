package org.uma.jmetal.problem.permutationproblem.impl;

import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

/** Fake class representing a integer permutation problem. Intended to be used in unit tests. */
public class FakeIntegerPermutationProblem extends AbstractIntegerPermutationProblem {
  private int permutationLength ;
  private int numberOfObjectives ;

  /** Constructor */
  public FakeIntegerPermutationProblem(int permutationLength, int numberOfObjectives) {
    this.permutationLength = permutationLength;
    this.numberOfObjectives = numberOfObjectives ;
  }

  @Override
  public int numberOfVariables() {
    return permutationLength ;
  }

  @Override
  public int numberOfObjectives() {
    return numberOfObjectives ;
  }

  @Override
  public int numberOfConstraints() {
    return 0 ;
  }

  @Override
  public String name() {
    return "FakeIntegerPermutationProblem";
  }

  @Override
  public PermutationSolution<Integer> evaluate(PermutationSolution<Integer> solution) {
    return solution;
  }

  @Override
  public int length() {
    return numberOfVariables();
  }
}
