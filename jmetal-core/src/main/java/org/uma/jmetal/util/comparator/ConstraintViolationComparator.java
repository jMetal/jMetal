package org.uma.jmetal.util.comparator;

import org.uma.jmetal.solution.Solution;

import java.util.Comparator;

public interface ConstraintViolationComparator extends Comparator<Solution> {
  public int compare(Solution o1, Solution o2);
}
