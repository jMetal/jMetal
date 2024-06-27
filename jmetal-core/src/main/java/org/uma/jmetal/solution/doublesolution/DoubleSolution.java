package org.uma.jmetal.solution.doublesolution;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.bounds.Bounds;

/**
 * Interface representing double solutions, where the variables are a list of bounded double values.
 * Each double variable has associated a {@link Bounds<Double>} object representing its lower and upper bounds.
 *
 * @author Antonio J. Nebro
 */
public interface DoubleSolution extends Solution<Double> {
  Bounds<Double> getBounds(int index) ;
}
