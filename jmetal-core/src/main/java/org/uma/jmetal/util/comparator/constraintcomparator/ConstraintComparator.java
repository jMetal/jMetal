package org.uma.jmetal.util.comparator.constraintcomparator;

import java.util.Comparator;
import org.uma.jmetal.solution.Solution;

/**
 * Interface representing constraint comparators
 *
 * @param <S> Solution
 */
public interface ConstraintComparator <S extends Solution<?>> extends Comparator<S> {
}
