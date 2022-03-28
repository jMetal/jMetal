package org.uma.jmetal.util.comparator;

import java.io.Serializable;
import java.util.Comparator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements a solution comparator taking into account the violation constraints
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DominanceWithConstraintsComparator<S extends Solution<?>> implements Comparator<S>, Serializable {
  private OverallConstraintViolationDegreeComparator<S> constraintViolationComparator;

  /** Constructor */
  public DominanceWithConstraintsComparator() {
    this(new OverallConstraintViolationDegreeComparator<S>());
  }

  /** Constructor */
  public DominanceWithConstraintsComparator(OverallConstraintViolationDegreeComparator<S> constraintComparator) {
    this.constraintViolationComparator = constraintComparator;
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
    Check.notNull(solution1);
    Check.notNull(solution2);
    Check.that(
        solution1.objectives().length == solution2.objectives().length,
        "Cannot compare because solution1 has "
            + solution1.objectives().length
            + " objectives and solution2 has "
            + solution2.objectives().length);

    int result;
    result = constraintViolationComparator.compare(solution1, solution2);
    if (result == 0) {
      result = VectorUtils.dominanceTest(solution1.objectives(), solution2.objectives());
    }

    return result;
  }
}
