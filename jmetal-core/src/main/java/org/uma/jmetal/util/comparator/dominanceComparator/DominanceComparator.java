package org.uma.jmetal.util.comparator.dominanceComparator;

import java.util.Comparator;
import org.uma.jmetal.solution.Solution;

/**
 * Interface representing dominance comparators
 * @param <S>
 */
public interface DominanceComparator <S extends Solution<?>> extends Comparator<S> {
}
