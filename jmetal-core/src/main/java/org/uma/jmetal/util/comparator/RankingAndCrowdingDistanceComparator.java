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

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @version 1.0
 *
 * This class implements a comparator based on the rank of the solutions; if the rank is the same
 * then the crowding distance is used.
 */
public class RankingAndCrowdingDistanceComparator<S extends Solution<?>> implements Comparator<S>, Serializable {
  private final Comparator<S> rankComparator = new RankingComparator<S>();
  private final Comparator<S> crowdingDistanceComparator = new CrowdingDistanceComparator<S>() ;

  /**
   * Compares two solutions.
   *
   * @param solution1 Object representing the first solution
   * @param solution2 Object representing the second solution.
   * @return -1, or 0, or 1 if solution1 is less than, equal, or greater than solution2,
   * respectively.
   */
  @Override
  public int compare(S solution1, S solution2) {
    int result = rankComparator.compare(solution1, solution2) ;
    if (result == 0) {
      result = crowdingDistanceComparator.compare(solution1, solution2);
    }

    return result;
  }
}
