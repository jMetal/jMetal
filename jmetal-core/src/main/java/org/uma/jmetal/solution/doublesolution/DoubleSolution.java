package org.uma.jmetal.solution.doublesolution;

import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.bounds.Bounds;

/**
 * Interface representing double solutions, where the variables are a list of bounded double values.
 * Each double variable has associated a {@Link Bounds<Double>} object representing its lower and upper bounds.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DoubleSolution extends Solution<Double> {
  @Nullable Bounds<Double> getBounds(int index) ;
}
