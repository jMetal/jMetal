package org.uma.jmetal.util.comparator;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author Antonio J. Nebro
 *     <p>This class implements a comparator based on the rank of the solutions.
 */
@SuppressWarnings("serial")
public class RankingComparator<S extends Solution<?>> implements Comparator<S>, Serializable {
  private Ranking<S> ranking = new DominanceRanking<S>();

  /**
   * Compares two solutions according to the ranking attribute. The lower the ranking the better
   *
   * @param solution1 Object representing the first solution.
   * @param solution2 Object representing the second solution.
   * @return -1, or 0, or 1 if o1 is less than, equal, or greater than o2, respectively.
   */
  @Override
  public int compare(S solution1, S solution2) {
    int result;
    if (solution1 == null) {
      if (solution2 == null) {
        result = 0;
      } else {
        result = 1;
      }
    } else if (solution2 == null) {
      result = -1;
    } else {
      int rank1 = Integer.MAX_VALUE;
      int rank2 = Integer.MAX_VALUE;

      if (ranking.getAttribute(solution1) != null) {
        rank1 = (int) ranking.getAttribute(solution1);
      }

      if (ranking.getAttribute(solution2) != null) {
        rank2 = (int) ranking.getAttribute(solution2);
      }

      if (rank1 < rank2) {
        result = -1;
      } else if (rank1 > rank2) {
        result = 1;
      } else {
        result = 0;
      }
    }

    return result;
  }
}
