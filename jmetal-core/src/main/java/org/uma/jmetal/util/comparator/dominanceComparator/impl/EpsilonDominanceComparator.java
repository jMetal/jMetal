package org.uma.jmetal.util.comparator.dominanceComparator.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.dominanceComparator.DominanceComparator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Epsilon-dominance comparator (Laumanns et al., 2002): the objective space is divided into boxes
 * of side epsilon, and a solution dominates another if its box dominates the box of the other. When
 * both solutions are in the same box, the one closer to the lower corner of the box is preferred;
 * if they are at the same distance, the comparison returns 1, so that an archive keeps the
 * solution that it already has. It does not take the constraints into account.
 *
 * @author Antonio J. Nebro
 */
public class EpsilonDominanceComparator<S extends Solution<?>> implements DominanceComparator<S> {
  private final double epsilon ;

  /**
   * Constructor.
   *
   * @param epsilon the side of the boxes, which must be positive
   */
  public EpsilonDominanceComparator(double epsilon) {
    Check.that(epsilon > 0.0, () -> "The epsilon value must be positive: " + epsilon);
    this.epsilon = epsilon ;
  }

  /**
   * Compares two solutions.
   *
   * @param solution1 Object representing the first <code>Solution</code>.
   * @param solution2 Object representing the second <code>Solution</code>.
   * @return -1, or 0, or 1 if solution1 dominates solution2, both are
   * non-dominated, or solution1  is dominated by solution2, respectively.
   */
  @Override
  public int compare(S solution1, S solution2) {
    Check.notNull(solution1);
    Check.notNull(solution2);
    Check.that(
            solution1.objectives().length == solution2.objectives().length,
            () -> "Cannot compare because solution1 has "
                    + solution1.objectives().length
                    + " objectives and solution2 has "
                    + solution2.objectives().length);

    return dominanceTest(solution1, solution2) ;
  }

  private int dominanceTest(Solution<?> solution1, Solution<?> solution2) {
    boolean bestIsOne = false ;
    boolean bestIsTwo = false ;
    for (int i = 0; i < solution1.objectives().length; i++) {
      double value1 = Math.floor(solution1.objectives()[i] / epsilon);
      double value2 = Math.floor(solution2.objectives()[i] / epsilon);
      if (value1 < value2) {
        bestIsOne = true;

        if (bestIsTwo) {
          return 0;
        }
      } else if (value2 < value1) {
        bestIsTwo = true;

        if (bestIsOne) {
          return 0 ;
        }
      }
    }
    if (!bestIsOne && !bestIsTwo) {
      double dist1 = 0.0;
      double dist2 = 0.0;

      for (int i = 0; i < solution1.objectives().length; i++) {
        double index1 = Math.floor(solution1.objectives()[i] / epsilon);
        double index2 = Math.floor(solution2.objectives()[i] / epsilon);

        dist1 += Math.pow(solution1.objectives()[i] - index1 * epsilon,
                2.0);
        dist2 += Math.pow(solution2.objectives()[i] - index2 * epsilon,
                2.0);
      }

      if (dist1 < dist2) {
        return -1;
      } else {
        return 1;
      }
    } else if (bestIsTwo) {
      return 1;
    } else {
      return -1;
    }
  }
}
