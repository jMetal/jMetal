package org.uma.jmetal.solution.doublesolution;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.bounds.Bounds;

/**
 * Interface representing a double solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DoubleSolution extends Solution<Double> {
  /**
   * @deprecated Use {@link #getBounds(int)}{@link Bounds#getLowerBound()
   *             .getLowerBound()} instead.
   */
  @Deprecated
  Double getLowerBound(int index) ;
  /**
   * @deprecated Use {@link #getBounds(int)}{@link Bounds#getLowerBound()
   *             .getLowerBound()} instead.
   */
  @Deprecated
  Double getUpperBound(int index) ;
  
  /**
   * It is often the case that we use both bounds together. Searching twice the
   * same index may be counter productive in this case. This methods allows to
   * offer this optimization, although its default implementation just delegates
   * to the separate methods.
   */
  default Bounds<Double> getBounds(int index) {
    DoubleSolution solution = this;
    Double lowerBound = solution.getLowerBound(index);
    Double upperBound = solution.getUpperBound(index);
    return Bounds.create(lowerBound, upperBound);
  }
}
