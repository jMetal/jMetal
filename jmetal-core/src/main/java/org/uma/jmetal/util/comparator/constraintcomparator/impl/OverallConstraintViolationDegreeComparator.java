package org.uma.jmetal.util.comparator.constraintcomparator.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.comparator.constraintcomparator.ConstraintComparator;

/**
 * This class implements a <code>Comparator</code> (a method for comparing <code>Solution</code> objects)
 * based on the overall constraint violation of the solutions, as done in NSGA-II.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class OverallConstraintViolationDegreeComparator<S extends Solution<?>> implements
    ConstraintComparator<S> {
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
    var violationDegreeSolution1 = ConstraintHandling.overallConstraintViolationDegree(solution1);
    var violationDegreeSolution2 = ConstraintHandling.overallConstraintViolationDegree(solution2);

    if ((violationDegreeSolution1 < 0.0) && (violationDegreeSolution2 < 0.0)) {
      return Double.compare(violationDegreeSolution2, violationDegreeSolution1);
    } else if ((violationDegreeSolution1 == 0.0) && (violationDegreeSolution2 < 0.0)) {
      return -1;
    } else if ((violationDegreeSolution1 < 0.0) && (violationDegreeSolution2 == 0.0)) {
      return 1;
    } else {
      return 0;
    }
  }
}
