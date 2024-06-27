package org.uma.jmetal.util.comparator.dominanceComparator.impl;

import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.dominanceComparator.DominanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * This class implements a solution comparator according to the concept of g-dominance
 * (https://doi.org/10.1016/j.ejor.2008.07.015)
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class GDominanceComparator<S extends Solution<?>> implements DominanceComparator<S> {

  private List<Double> referencePoint ;
  private DominanceWithConstraintsComparator<S> dominanceComparator ;

  /** Constructor */
  public GDominanceComparator(List<Double> referencePoint) {
    this.referencePoint = referencePoint ;
    dominanceComparator = new DominanceWithConstraintsComparator<>() ;
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
    } else if (solution1.objectives().length != solution2.objectives().length) {
      throw new JMetalException("Cannot compare because solution1 has " +
          solution1.objectives().length+ " objectives and solution2 has " +
          solution2.objectives().length) ;
    }

    int result = flagComparison(solution1, solution2);

    return result ;
  }

  private int flagComparison(S solution1, S solution2) {
    int result ;
    if (flag(solution1) > flag(solution2)) {
      result = -1 ;
    } else if (flag(solution1) < flag(solution2)) {
      result = 1 ;
    } else {
      result = dominanceComparator.compare(solution1, solution2) ;
    }

    return result ;
  }

  private int flag(S solution) {
    int result = 1 ;
    for (int i = 0; i < solution.objectives().length; i++) {
      if (solution.objectives()[i] > referencePoint.get(i)) {
        result = 0 ;
      }
    }
    if (result == 0) {
      result = 1 ;
      for (int i = 0; i < solution.objectives().length; i++) {
        if (solution.objectives()[i] < referencePoint.get(i)) {
          result = 0 ;
        }
      }
    }

    return result ;
  }
}
