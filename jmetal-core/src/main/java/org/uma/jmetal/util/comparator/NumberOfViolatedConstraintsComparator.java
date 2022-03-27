package org.uma.jmetal.util.comparator;

import java.util.Comparator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;

/**
 * This class implements a <code>Comparator</code> (a method for comparing <code>Solution</code> objects)
 * based on the number of violated constraints of solutions.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class NumberOfViolatedConstraintsComparator<S extends Solution<?>> implements Comparator<S> {
  /**
   * Compares two solutions. If the solutions has no constraints the method return 0
   *
   * @param solution1 Object representing the first <code>Solution</code>.
   * @param solution2 Object representing the second <code>Solution</code>.
   * @return -1 if the number of violated constraints of solution1 is higher than the one of solution2, 1 in
   * the opposite case, and 0 if they have the same value (this case applies when the two compared solutions
   * have no constraints).
   */
  public int compare(S solution1, S solution2) {
    var numberOfViolatedConstraintsOfSolution1 = ConstraintHandling.numberOfViolatedConstraints(solution1);
    var numberOfViolatedConstraintsOfSolution2 = ConstraintHandling.numberOfViolatedConstraints(solution2);

    return Integer.compare(numberOfViolatedConstraintsOfSolution1, numberOfViolatedConstraintsOfSolution2) ;
    /*
    if ((numberOfViolatedConstraintsOfSolution1 > 0) && (numberOfViolatedConstraintsOfSolution2 > 0)) {
      return Double.compare(numberOfViolatedConstraintsOfSolution2, numberOfViolatedConstraintsOfSolution1);
    } else if ((numberOfViolatedConstraintsOfSolution1 == 0) && (numberOfViolatedConstraintsOfSolution2 < 0)) {
      return -1;
    } else if ((numberOfViolatedConstraintsOfSolution1 < 0) && (numberOfViolatedConstraintsOfSolution2 == 0)) {
      return 1;
    } else {
      return 0;
    }

     */
  }
}
