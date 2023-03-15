package org.uma.jmetal.problem.permutationproblem.impl;

import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;

@SuppressWarnings("serial")
public abstract class AbstractIntegerPermutationProblem
    implements PermutationProblem<PermutationSolution<Integer>> {

  @Override
  public PermutationSolution<Integer> createSolution() {
    return new IntegerPermutationSolution(length(), numberOfObjectives(), numberOfConstraints()) ;
  }

  @Override
  public int length() {
    return numberOfVariables();
  }
}
