package org.uma.jmetal.component.catalogue.common.termination;

import java.util.Map;
import java.util.OptionalDouble;

/**
 * This interface represents classes that check the termination condition of an algorithm.
 *
 * @author Antonio J. Nebro
 */
@FunctionalInterface
public interface Termination {
  boolean isMet(Map<String, Object> algorithmStatusData) ;

  /**
   * The fraction of the run that has been done, in [0, 1], if this termination condition knows
   * it; by default, it does not. Components that change during the run, such as some
   * constraint-handling techniques, use it.
   *
   * @param algorithmStatusData the status of the algorithm
   * @return the progress of the run, or an empty value
   */
  default OptionalDouble progress(Map<String, Object> algorithmStatusData) {
    return OptionalDouble.empty();
  }
}
