package org.uma.jmetal.util.comparator.dominanceComparator.impl;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.dominanceComparator.DominanceComparator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements a solution comparator taking into account the violation constraints and
 * an optional epsilon value (i.e, implements an epsilon dominance comparator)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class EpsilonDominanceComparator<S extends Solution<?>> implements DominanceComparator<S> {
  private final double epsilon ;

  public EpsilonDominanceComparator(double epsilon) {
    this.epsilon = epsilon ;
  }

  public EpsilonDominanceComparator() {
    this(0.0) ;
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
  public int compare(S solution1, @NotNull S solution2) {
    Check.notNull(solution1);
    Check.notNull(solution2);
    Check.that(
            solution1.objectives().length == solution2.objectives().length,
            "Cannot compare because solution1 has "
                    + solution1.objectives().length
                    + " objectives and solution2 has "
                    + solution2.objectives().length);

    return dominanceTest(solution1, solution2) ;
  }

  private int dominanceTest(@NotNull Solution<?> solution1, Solution<?> solution2) {
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
