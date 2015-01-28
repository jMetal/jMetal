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
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;

import java.util.Comparator;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * Compares two solutions according to the crowding distance attribute. The higher
 * the distance the better
 */
public class CrowdingDistanceComparator implements Comparator<Solution> {
  private final CrowdingDistance crowdingDistance = new CrowdingDistance() ;

  /**
   * Compare two solutions.
   *
   * @param solution1 Object representing the first <code>Solution</code>.
   * @param solution2 Object representing the second <code>Solution</code>.
   * @return -1, or 0, or 1 if solution1 is has greater, equal, or less distance value than solution2,
   * respectively.
   */
  @Override
  public int compare(Solution solution1, Solution solution2) {
    int result ;
    if (solution1 == null) {
      if (solution2 == null) {
        result = 0;
      } else {
        result = 1 ;
      }
    } else if (solution2 == null) {
      result = -1;
    } else {
      double distance1 = Double.MIN_VALUE ;
      double distance2 = Double.MIN_VALUE ;

      if (crowdingDistance.getAttribute(solution1) != null) {
        distance1 = crowdingDistance.getAttribute(solution1) ;
      }

      if (crowdingDistance.getAttribute(solution2) != null) {
        distance2 = crowdingDistance.getAttribute(solution2) ;
      }

      if (distance1 > distance2) {
        result = -1;
      } else  if (distance1 < distance2) {
        result = 1;
      } else {
        return 0;
      }
    }

    return result ;
  }
}
