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

package org.uma.jmetal.util.archive.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AdaptiveGrid;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * This class implements an archive (solution list) based on an adaptive grid used in PAES
 */
public class AdaptiveGridArchive<S extends Solution> implements BoundedArchive<S> {

  private AdaptiveGrid<S> grid;
  private List<S> solutionList;

  private int maxSize;
  private Comparator<Solution> dominanceComparator;

  /**
   * Constructor.
   *
   * @param maxSize    The maximum size of the setArchive
   * @param bisections The maximum number of bi-divisions for the adaptive
   *                   grid.
   * @param objectives The number of objectives.
   */
  public AdaptiveGridArchive(int maxSize, int bisections, int objectives) {
    this.maxSize = maxSize;
    dominanceComparator = new DominanceComparator();
    grid = new AdaptiveGrid(bisections, objectives);
    solutionList = new ArrayList<>(maxSize) ;
  }

  /**
   * Adds a <code>Solution</code> to the setArchive. If the <code>Solution</code>
   * is dominated by any member of the setArchive then it is discarded. If the
   * <code>Solution</code> dominates some members of the setArchive, these are
   * removed. If the setArchive is full and the <code>Solution</code> has to be
   * inserted, one <code>Solution</code> of the most populated hypercube of the
   * adaptive grid is removed.
   *
   * @param solution The <code>Solution</code>
   * @return true if the <code>Solution</code> has been inserted, false
   * otherwise.
   */
  public boolean add(S solution) {
    //Iterator of individuals over the list
    Iterator<S> iterator = solutionList.iterator();

    while (iterator.hasNext()) {
      S element = iterator.next();
      int flag = dominanceComparator.compare(solution, element);
      if (flag == -1) { // The Individual to insert dominates other
        // individuals in  the setArchive
        iterator.remove(); //Delete it from the setArchive
        int location = grid.location(element);
        if (grid.getLocationDensity(location) > 1) {//The hypercube contains
          grid.removeSolution(location);            //more than one individual
        } else {
          grid.updateGrid(solutionList);
        }
      }
      else if (flag == 1) { // An Individual into the file dominates the
        // solution to insert
        return false; // The solution will not be inserted
      }
    }

    // At this point, the solutiontype may be inserted
    if (solutionList.size() == 0) { //The setArchive is empty
      solutionList.add(solution);
      grid.updateGrid(solutionList);
      return true;
    }

    if (solutionList.size() < maxSize) { //The setArchive is not full
      grid.updateGrid(solution, solutionList); // Update the grid if applicable
      int location;
      location = grid.location(solution); // Get the location of the solutiontype
      grid.addSolution(location); // Increment the density of the hypercube
      solutionList.add(solution); // Add the solutiontype to the list
      return true;
    }

    // At this point, the solutiontype has to be inserted and the setArchive is full
    grid.updateGrid(solution, solutionList);
    int location = grid.location(solution);
    if (location == grid.getMostPopulatedHypercube()) { // The solutiontype is in the
      // most populated hypercube
      return false; // Not inserted
    } else {
      // Remove an solutiontype from most populated area
      iterator = solutionList.iterator();
      boolean removed = false;
      while (iterator.hasNext()) {
        if (!removed) {
          S element = iterator.next();
          int location2 = grid.location(element);
          if (location2 == grid.getMostPopulatedHypercube()) {
            iterator.remove();
            grid.removeSolution(location2);
          }
        }
      }
      // A solution from most populated hypercube has been removed,
      // insert now the solution
      grid.addSolution(location);
      solutionList.add(solution);
    }
    return true;
  }

  public AdaptiveGrid getGrid() {
    return grid;
  }

  @Override
  public List<S> getSolutionList() {
    return solutionList;
  }

  @Override
  public int getMaxSize() {
    return maxSize ;
  }
}
