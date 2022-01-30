package org.uma.jmetal.solution.doublesolution;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.bounds.Bounds;

/**
 * Interface representing double solutions. Each double variable has associated a {@Link Bounds<Double>} object representing
 * its lower and upper bounds.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DoubleSolution extends Solution<Double> {
  Bounds<Double> getBounds(int index) ;
}
