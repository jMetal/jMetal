package org.uma.jmetal.solution.permutationsolution;

import org.uma.jmetal.solution.Solution;

/**
 * Interface representing permutation based solutions
 *
 * @author Antonio J. Nebro
 */
public interface PermutationSolution<T> extends Solution<T> {
  int getLength() ;
}
