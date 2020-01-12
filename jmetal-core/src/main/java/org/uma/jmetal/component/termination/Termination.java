package org.uma.jmetal.component.termination;

import java.util.Map;

/**
 * This interface represents classes that isMet the termination condition of an algorithm.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@FunctionalInterface
public interface Termination {
  boolean isMet(Map<String, Object> algorithmStatusData) ;
}
