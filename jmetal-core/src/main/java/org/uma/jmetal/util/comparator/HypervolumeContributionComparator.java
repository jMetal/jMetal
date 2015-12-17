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
import org.uma.jmetal.util.solutionattribute.impl.HypervolumeContributionAttribute;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Compares two solutions according to the crowding distance attribute. The higher
 * the distance the better
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class HypervolumeContributionComparator<S extends Solution<?>> implements Comparator<S>, Serializable {
  private final HypervolumeContributionAttribute<S> hvContribution = new HypervolumeContributionAttribute<S>() ;

  /**
   * Compare two solutions.
   *
   * @param solution1 Object representing the first <code>Solution</code>.
   * @param solution2 Object representing the second <code>Solution</code>.
   * @return -1, or 0, or 1 if solution1 is has lower, equal, or higher contribution value than solution2,
   * respectively.
   */
  @Override
  public int compare(S solution1, S solution2) {
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
      double contribution1 = Double.MAX_VALUE ;
      double contribution2 = Double.MAX_VALUE ;

      if (hvContribution.getAttribute(solution1) != null) {
        contribution1 = (double) hvContribution.getAttribute(solution1);
      }

      if (hvContribution.getAttribute(solution2) != null) {
        contribution2 = (double) hvContribution.getAttribute(solution2);
      }

      if (contribution1 < contribution2) {
        result = 1;
      } else  if (contribution1 > contribution2) {
        result = -1;
      } else {
        result = 0;
      }
    }

    return result ;
  }
}
