package org.uma.jmetal.util.comparator.dominanceComparator.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.comparator.dominanceComparator.DominanceComparator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements a solution comparator for dominance checking
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultDominanceComparator<S extends Solution<?>> implements DominanceComparator<S> {
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
    Check.notNull(solution1);
    Check.notNull(solution2);
    Check.that(
        solution1.objectives().length == solution2.objectives().length,
        "Cannot compare because solution1 has "
            + solution1.objectives().length
            + " objectives and solution2 has "
            + solution2.objectives().length);

    return VectorUtils.dominanceTest(solution1.objectives(), solution2.objectives()) ;
  }
}
