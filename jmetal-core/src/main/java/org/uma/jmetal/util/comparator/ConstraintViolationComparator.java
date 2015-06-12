package org.uma.jmetal.util.comparator;

import org.uma.jmetal.solution.Solution;

import java.util.Comparator;

public interface ConstraintViolationComparator<S extends Solution<?>> extends Comparator<S> {
  public int compare(S solution1, S solution2);
}
