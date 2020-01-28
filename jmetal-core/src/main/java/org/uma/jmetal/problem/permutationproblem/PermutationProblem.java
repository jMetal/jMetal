package org.uma.jmetal.problem.permutationproblem;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

/**
 * Interface representing permutation problems
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface PermutationProblem<S extends PermutationSolution<?>> extends Problem<S> {
  int getLength() ;
}
