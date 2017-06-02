package org.uma.jmetal.util.comparator;

import org.uma.jmetal.solution.Solution;

import java.io.Serializable;
import java.util.Comparator;

/**
 * This class implements a <code>Comparator</code> (a method for comparing
 * <code>Solution</code> objects) based whether all the objective values are
 * equal or not. A dominance test is applied to decide about what solution
 * is the best.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class EqualSolutionsComparator<S extends Solution<?>> implements Comparator<S>, Serializable {

  /**
   * Compares two solutions.
   *
   * @param solution1 First <code>Solution</code>.
   * @param solution2 Second <code>Solution</code>.
   * @return -1, or 0, or 1, or 2 if solution1 is dominates solution2, solution1
   * and solution2 are equals, or solution1 is greater than solution2,
   * respectively.
   */
  @Override
  public int compare(S solution1, S solution2) {
    if (solution1 == null) {
      return 1;
    } else if (solution2 == null) {
      return -1;
    }

    int dominate1; // dominate1 indicates if some objective of solution1
    // dominates the same objective in solution2. dominate2
    int dominate2; // is the complementary of dominate1.

    dominate1 = 0;
    dominate2 = 0;

    int flag;
    double value1, value2;
    for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
      value1 = solution1.getObjective(i);
      value2 = solution2.getObjective(i);

      if (value1 < value2) {
        flag = -1;
      } else if (value1 > value2) {
        flag = 1;
      } else {
        flag = 0;
      }

      if (flag == -1) {
        dominate1 = 1;
      }

      if (flag == 1) {
        dominate2 = 1;
      }
    }

    if (dominate1 == 0 && dominate2 == 0) {
      //No one dominates the other
      return 0;
    }

    if (dominate1 == 1) {
      // solution1 dominates
      return -1;
    } else if (dominate2 == 1) {
      // solution2 dominates
      return 1;
    }
    return 2;
  }
}

