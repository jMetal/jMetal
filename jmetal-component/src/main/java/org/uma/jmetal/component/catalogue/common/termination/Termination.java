package org.uma.jmetal.component.catalogue.common.termination;

import java.util.Map;

/**
 * This interface represents classes that check the termination condition of an algorithm.
 *
 * @author Antonio J. Nebro
 */
@FunctionalInterface
public interface Termination {
  boolean isMet(Map<String, Object> algorithmStatusData) ;
}
