package org.uma.jmetal.solution.integersolution;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.bounds.Bounds;

/**
 * Interface representing a integer solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface IntegerSolution extends Solution<Integer> {
  /**
   * @deprecated Use {@link #getBounds(int)}{@link Bounds#getLowerBound()
   *             .getLowerBound()} instead.
   */
  @Deprecated
  Integer getLowerBound(int index) ;
  /**
   * @deprecated Use {@link #getBounds(int)}{@link Bounds#getUpperBound()
   *             .getUpperBound()} instead.
   */
  @Deprecated
  Integer getUpperBound(int index) ;
  
  /**
   * It is often the case that we use both bounds together. Searching twice the
   * same index may be counter productive in this case. This methods allows to
   * offer this optimization, although its default implementation just delegates
   * to the separate methods.
   */
  default Bounds<Integer> getBounds(int index) {
    IntegerSolution solution = this;
    Integer lowerBound = solution.getLowerBound(index);
    Integer upperBound = solution.getUpperBound(index);
    return Bounds.create(lowerBound, upperBound);
  }
}
