package org.uma.jmetal.solution.integersolution;

import org.uma.jmetal.solution.Solution;

/**
 * Interface representing a integer solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface IntegerSolution extends Solution<Integer> {
  Integer getLowerBound(int index) ;
  Integer getUpperBound(int index) ;
}
