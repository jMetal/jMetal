package org.uma.jmetal.util.comparator.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.comparator.ConstraintViolationComparator;

/**
 * This class implements a <code>Comparator</code> (a method for comparing <code>Solution</code> objects)
 * based on the overall constraint violation of the solutions, as done in NSGA-II.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class OverallConstraintViolationComparator<S extends Solution<?>>
    implements ConstraintViolationComparator<S> {
  /**
   * Compares two solutions. If the solutions has no constraints the method return 0
   *
   * @param solution1 Object representing the first <code>Solution</code>.
   * @param solution2 Object representing the second <code>Solution</code>.
   * @return -1 if the overall constraint violation degree of solution1 is higher than the one of solution2, 1 in
   * the opposite case, and 0 if they have the same value (this case applies when the two compared solutions
   * have no constraints). Note that the violation degree is a negative number, so when comparing to two solutions,
   * the higher the value the better.
   */
  public int compare(S solution1, S solution2) {
    double violationDegreeSolution1 ;
    double violationDegreeSolution2;

    violationDegreeSolution1 = ConstraintHandling.overallConstraintViolationDegree(solution1);
    violationDegreeSolution2 = ConstraintHandling.overallConstraintViolationDegree(solution2);

    if ((violationDegreeSolution1 < 0) && (violationDegreeSolution2 < 0)) {
      if (violationDegreeSolution1 > violationDegreeSolution2) {
        return -1;
      } else if (violationDegreeSolution2 > violationDegreeSolution1) {
        return 1;
      } else {
        return 0;
      }
    } else if ((violationDegreeSolution1 == 0) && (violationDegreeSolution2 < 0)) {
      return -1;
    } else if ((violationDegreeSolution1 < 0) && (violationDegreeSolution2 == 0)) {
      return 1;
    } else {
      return 0;
    }
  }
}
