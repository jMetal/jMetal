package org.uma.jmetal.problem.permutationproblem.impl;

import org.uma.jmetal.problem.AbstractGenericProblem;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;

@SuppressWarnings("serial")
public abstract class AbstractIntegerPermutationProblem
    extends AbstractGenericProblem<PermutationSolution<Integer>> implements
    PermutationProblem<PermutationSolution<Integer>> {

  @Override
  public PermutationSolution<Integer> createSolution() {
    return new IntegerPermutationSolution(getLength(), getNumberOfObjectives()) ;
  }
}
