package org.uma.jmetal.problem;

import org.uma.jmetal.solution.PermutationSolution;

/** Interface representing permutation problems */
public interface PermutationProblem<S extends PermutationSolution<?>> extends Problem<S> {
  public int getPermutationLength(int index) ;
}
