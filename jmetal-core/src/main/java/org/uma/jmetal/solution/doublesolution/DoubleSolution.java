package org.uma.jmetal.solution.doublesolution;

import org.uma.jmetal.solution.Solution;

/**
 * Interface representing a double solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DoubleSolution extends Solution<Double> {
  Double getLowerBound(int index) ;
  Double getUpperBound(int index) ;
}
