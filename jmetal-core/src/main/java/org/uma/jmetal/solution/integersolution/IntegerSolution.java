package org.uma.jmetal.solution.integersolution;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.bounds.Bounds;

/**
 * Interface representing a integer solutions. Each integer variable has associated a {@Link Bounds<Integer>} object representing
 * its lower and upper bounds.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface IntegerSolution extends Solution<Integer> {
  Bounds<Integer> getBounds(int index) ;
}
