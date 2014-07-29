//  AdaptiveGridArchive.java
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
import org.uma.jmetal.util.AdaptiveGrid;
import org.uma.jmetal.util.comparator.DominanceComparator;

import java.util.Comparator;
import java.util.Iterator;

/**
 * This class implements an archive based on an adaptive grid used in PAES
 */
public class AdaptiveGridArchive extends Archive {
  private static final long serialVersionUID = -1265707693992803196L;

  private AdaptiveGrid grid;
  private int maxSize;
  private Comparator<Solution> dominanceComparator;

  /**
   * Constructor.
   *
   * @param maxSize    The maximum size of the archive
   * @param bisections The maximum number of bi-divisions for the adaptive
   *                   grid.
   * @param objectives The number of objectives.
   */
  public AdaptiveGridArchive(int maxSize, int bisections, int objectives) {
    super(maxSize);
    this.maxSize = maxSize;
    dominanceComparator = new DominanceComparator();
    grid = new AdaptiveGrid(bisections, objectives);
  }

  /**
   * Adds a <code>Solution</code> to the archive. If the <code>Solution</code>
   * is dominated by any member of the archive then it is discarded. If the
   * <code>Solution</code> dominates some members of the archive, these are
   * removed. If the archive is full and the <code>Solution</code> has to be
   * inserted, one <code>Solution</code> of the most populated hypercube of the
   * adaptive grid is removed.
   *
   * @param solution The <code>Solution</code>
   * @return true if the <code>Solution</code> has been inserted, false
   * otherwise.
   */
  public boolean add(Solution solution) {
    //Iterator of individuals over the list
    Iterator<Solution> iterator = solutionsList.iterator();

    while (iterator.hasNext()) {
      Solution element = iterator.next();
      int flag = dominanceComparator.compare(solution, element);
      if (flag == -1) { // The Individual to insert dominates other
        // individuals in  the archive
        iterator.remove(); //Delete it from the archive
        int location = grid.location(element);
        if (grid.getLocationDensity(location) > 1) {//The hypercube contains
          grid.removeSolution(location);            //more than one individual
        } else {
          grid.updateGrid(this);
        }
      }
      else if (flag == 1) { // An Individual into the file dominates the
        // solution to insert
        return false; // The solution will not be inserted
      }
    }

    // At this point, the solutiontype may be inserted
    if (size() == 0) { //The archive is empty
      solutionsList.add(solution);
      grid.updateGrid(this);
      return true;
    }

    if (size() < maxSize) { //The archive is not full
      grid.updateGrid(solution, this); // Update the grid if applicable
      int location;
      location = grid.location(solution); // Get the location of the solutiontype
      grid.addSolution(location); // Increment the density of the hypercube
      solutionsList.add(solution); // Add the solutiontype to the list
      return true;
    }

    // At this point, the solutiontype has to be inserted and the archive is full
    grid.updateGrid(solution, this);
    int location = grid.location(solution);
    if (location == grid.getMostPopulated()) { // The solutiontype is in the
      // most populated hypercube
      return false; // Not inserted
    } else {
      // Remove an solutiontype from most populated area
      iterator = solutionsList.iterator();
      boolean removed = false;
      while (iterator.hasNext()) {
        if (!removed) {
          Solution element = iterator.next();
          int location2 = grid.location(element);
          if (location2 == grid.getMostPopulated()) {
            iterator.remove();
            grid.removeSolution(location2);
          }
        }
      }
      // A solution from most populated hypercube has been removed,
      // insert now the solution
      grid.addSolution(location);
      solutionsList.add(solution);
    }
    return true;
  }

  public AdaptiveGrid getGrid() {
    return grid;
  }
}
