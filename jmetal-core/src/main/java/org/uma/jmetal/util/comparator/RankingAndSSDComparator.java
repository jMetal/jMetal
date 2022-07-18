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

import java.io.Serializable;
import java.util.Comparator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/**
 * @author Alejandro Santiago <aurelio.santiago@upalt.edu.mx>
 * @version 1.0
 *
 * This class implements a comparator based on the rank of the solutions; if the rank is the same
 * then the crowding distance is used.
 */
@SuppressWarnings("serial")
public class RankingAndSSDComparator<S extends Solution<?>> implements Comparator<S>, Serializable {
  private final Comparator<S> rankComparator ;
  private final Comparator<S> crowdingDistanceComparator  ;

  /**
   * Constructor
   */
  public RankingAndSSDComparator() {
    this(new FastNonDominatedSortRanking<>()) ;
  }

  public RankingAndSSDComparator(Ranking<S> ranking) {
    crowdingDistanceComparator = new SpatialSpreadDeviationComparator<>() ;
    rankComparator = Comparator.comparing(ranking::getRank) ;
  }


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
    var result = rankComparator.compare(solution1, solution2) ;
    if (result == 0) {
      result = crowdingDistanceComparator.compare(solution1, solution2);
    }

    return result;
  }
}
