//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.util.comparator;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.Comparator;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * This class implements a comparator based on the rank of the solutions.
 */
public class RankingComparator implements Comparator<Solution> {
  private Ranking ranking = new DominanceRanking() ;

  /**
   * Compares two solutions according to the ranking attribute. The lower the ranking the better
   *
   * @param solution1 Object representing the first solution.
   * @param solution2 Object representing the second solution.
   * @return -1, or 0, or 1 if o1 is less than, equal, or greater than o2,
   * respectively.
   */
  @Override
  public int compare(Solution solution1, Solution solution2) {
    int result ;
    if (solution1 == null) {
      if (solution2 == null) {
        result = 0;
      } else {
        result =  1;
      }
    } else if (solution2 == null) {
      result =  -1;
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
        result =  -1;
      } else if (rank1 > rank2) {
        result =  1;
      } else {
        result = 0;
      }
    }

    return result ;
  }
}
