package org.uma.jmetal.problem.impl;

import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.solution.impl.DefaultIntegerPermutationSolution;

@SuppressWarnings("serial")
public abstract class AbstractIntegerPermutationProblem
    extends AbstractGenericProblem<PermutationSolution<Integer>> implements
    PermutationProblem<PermutationSolution<Integer>> {

  /* Getters */

  /* Setters */

  @Override
  public PermutationSolution<Integer> createSolution() {
    return new DefaultIntegerPermutationSolution(this) ;
  }
}
