package org.uma.jmetal.util.comparator.constraintcomparator.impl;

import java.util.Comparator;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.comparator.constraintcomparator.ConstraintComparator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements a {@link Comparator} for {@link Solution} objects based on the number of
 * violated constraints of solutions.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class NumberOfViolatedConstraintsComparator<S extends Solution<?>> implements
    ConstraintComparator<S> {
  /**
   * Compares two solutions. If the solutions has no constraints the method return 0
   *
   * @param solution1 Object representing the first {@link Solution}.
   * @param solution2 Object representing the second {@link Solution}.
   * @return -1 if the number of violated constraints of solution1 is higher than the one of solution2, 1 in
   * the opposite case, and 0 if they have the same value (this case applies when the two compared solutions
   * have no constraints).
   */
  public int compare(@NotNull S solution1, S solution2) {
    Check.notNull(solution1);
    Check.notNull(solution2);
    var numberOfViolatedConstraintsOfSolution1 = ConstraintHandling.numberOfViolatedConstraints(solution1);
    var numberOfViolatedConstraintsOfSolution2 = ConstraintHandling.numberOfViolatedConstraints(solution2);

    return Integer.compare(numberOfViolatedConstraintsOfSolution1, numberOfViolatedConstraintsOfSolution2) ;
  }
}
