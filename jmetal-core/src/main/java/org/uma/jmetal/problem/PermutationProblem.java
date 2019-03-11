package org.uma.jmetal.problem;

import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

/**
 * Interface representing permutation problems
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface PermutationProblem<S extends PermutationSolution<?>> extends Problem<S> {
  public int getPermutationLength() ;
}
