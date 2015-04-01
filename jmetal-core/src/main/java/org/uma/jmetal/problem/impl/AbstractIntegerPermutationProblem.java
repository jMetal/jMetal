package org.uma.jmetal.problem.impl;

import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.solution.impl.GenericIntegerPermutationSolution;

import java.util.List;

public abstract class AbstractIntegerPermutationProblem
    extends AbstractGenericProblem<PermutationSolution<List<Integer>>> implements
    PermutationProblem<PermutationSolution<List<Integer>>> {

  /* Getters */

  /* Setters */

  @Override
  public PermutationSolution<List<Integer>> createSolution() {
    return new GenericIntegerPermutationSolution(this) ;
  }
}
