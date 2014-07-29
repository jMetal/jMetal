//  CrowdingArchive.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
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

package org.uma.jmetal.util.archive;

import org.uma.jmetal.core.Solution;
import org.uma.jmetal.util.Distance;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.EqualSolutions;

import java.util.Comparator;

/**
 * This class implements a bounded archive based on crowding distances (as
 * defined in NSGA-II).
 */
public class CrowdingArchive extends Archive {
  private static final long serialVersionUID = 4668820487667645467L;

  private int maxSize;
  private int objectives;
  private DominanceComparator dominanceComparator;
  private Comparator<Solution> equalsComparator;
  private Comparator<Solution> crowdingDistanceComparator;

  /**
   * Stores a <code>Distance</code> object, for distances utilities
   */
  private Distance distance;

  /** Constructor */
  public CrowdingArchive(int maxSize, int numberOfObjectives) {
    super(maxSize);
    this.maxSize = maxSize;
    objectives = numberOfObjectives;
    dominanceComparator = new DominanceComparator();
    equalsComparator = new EqualSolutions();
    crowdingDistanceComparator = new CrowdingDistanceComparator();
    distance = new Distance();
  }

  /**
   * Adds a <code>Solution</code> to the archive. If the <code>Solution</code>
   * is dominated by any member of the archive, then it is discarded. If the
   * <code>Solution</code> dominates some members of the archive, these are
   * removed. If the archive is full and the <code>Solution</code> has to be
   * inserted, the solutions are sorted by crowding distance and the one having
   * the minimum crowding distance value.
   *
   * @param solution The <code>Solution</code>
   * @return true if the <code>Solution</code> has been inserted, false
   * otherwise.
   * @throws org.uma.jmetal.util.JMetalException
   */
  public boolean add(Solution solution) throws JMetalException {
    int flag ;
    int i = 0;
    Solution aux;
    while (i < solutionsList.size()) {
      aux = solutionsList.get(i);

      flag = dominanceComparator.compare(solution, aux);
      if (flag == 1) {
        return false;
      } else if (flag == -1) {
        solutionsList.remove(i);
      } else {
        if (equalsComparator.compare(aux, solution) == 0) {
          return false;
        }
        i++;
      }
    }
    solutionsList.add(solution);
    if (size() > maxSize) { // FIXME: check whether the removed solution is the inserted one
      Distance.crowdingDistance(this);
      remove(indexWorst(crowdingDistanceComparator));
    }
    return true;
  }
}
