package org.uma.jmetal.util.comparator.dominanceComparator.impl;

import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.comparator.constraintcomparator.ConstraintComparator;
import org.uma.jmetal.util.comparator.constraintcomparator.impl.OverallConstraintViolationDegreeComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.DominanceComparator;

/**
 * This class implements a solution comparator taking into account the violation constraints
 *
 * @author Antonio J. Nebro
 */
public class DominanceWithConstraintsComparator<S extends Solution<?>> implements
    DominanceComparator<S> {
  private final MultiComparator<S> multiComparator ;

  /** Constructor */
  public DominanceWithConstraintsComparator() {
    this(new OverallConstraintViolationDegreeComparator<>());
  }

  /** Constructor */
  public DominanceWithConstraintsComparator(ConstraintComparator<S> constraintComparator) {
    multiComparator = new MultiComparator<>(List.of(constraintComparator, new DefaultDominanceComparator<>())) ;
  }

  /**
   * Compares two solutions.
   *
   * @param solution1 Object representing the first <code>Solution</code>.
   * @param solution2 Object representing the second <code>Solution</code>.
   * @return -1, or 0, or 1 if solution1 dominates solution2, both are non-dominated, or solution1
   *     is dominated by solution2, respectively.
   */
  @Override
  public int compare(S solution1, S solution2) {
    return multiComparator.compare(solution1, solution2);
  }
}
