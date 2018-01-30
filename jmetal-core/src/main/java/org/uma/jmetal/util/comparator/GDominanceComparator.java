package org.uma.jmetal.util.comparator;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;

import java.io.Serializable;
import java.util.Comparator;

/**
 * This class implements a solution comparator taking into account the violation constraints
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DominanceComparator<S extends Solution<?>> implements Comparator<S>, Serializable {
  private ConstraintViolationComparator<S> constraintViolationComparator;

  /** Constructor */
  public DominanceComparator() {
    this(new OverallConstraintViolationComparator<S>(), 0.0) ;
  }

  /** Constructor */
  public DominanceComparator(double epsilon) {
    this(new OverallConstraintViolationComparator<S>(), epsilon) ;
  }

  /** Constructor */
  public DominanceComparator(ConstraintViolationComparator<S> constraintComparator) {
    this(constraintComparator, 0.0) ;
  }

  /** Constructor */
  public DominanceComparator(ConstraintViolationComparator<S> constraintComparator, double epsilon) {
    constraintViolationComparator = constraintComparator ;
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
    if (solution1 == null) {
      throw new JMetalException("Solution1 is null") ;
    } else if (solution2 == null) {
      throw new JMetalException("Solution2 is null") ;
    } else if (solution1.getNumberOfObjectives() != solution2.getNumberOfObjectives()) {
      throw new JMetalException("Cannot compare because solution1 has " +
          solution1.getNumberOfObjectives()+ " objectives and solution2 has " +
          solution2.getNumberOfObjectives()) ;
    }
    int result ;
    result = constraintViolationComparator.compare(solution1, solution2) ;
    if (result == 0) {
      result = dominanceTest(solution1, solution2) ;
    }

    return result ;
  }

  private int dominanceTest(S solution1, S solution2) {
    int bestIsOne = 0 ;
    int bestIsTwo = 0 ;
    int result ;
    for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
      double value1 = solution1.getObjective(i);
      double value2 = solution2.getObjective(i);
      if (value1 != value2) {
        if (value1 < value2) {
          bestIsOne = 1;
        }
        if (value2 < value1) {
          bestIsTwo = 1;
        }
      }
    }
    if (bestIsOne > bestIsTwo) {
      result = -1;
    } else if (bestIsTwo > bestIsOne) {
      result = 1;
    } else {
      result = 0;
    }
    return result ;
  }
}
