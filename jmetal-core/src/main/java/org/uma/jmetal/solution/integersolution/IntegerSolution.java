package org.uma.jmetal.solution.integersolution;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.bounds.Bounds;

/**
 * Interface representing integer solutions, where the variables are a list of bounded integer values.
 * Each integer variable has associated a {@link Bounds<Integer>} object representing its lower and upper bounds.
 *
 * @author Antonio J. Nebro
 */
public interface IntegerSolution extends Solution<Integer> {
  Bounds<Integer> getBounds(int index) ;
}
