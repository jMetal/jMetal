package org.uma.jmetal3.problem;

import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.PermutationSolution;

/** Interface representing permutation problems */
public interface PermutationProblem<S extends PermutationSolution<?>> extends Problem<S> {
  public int getPermutationLength(int index) ;
}
