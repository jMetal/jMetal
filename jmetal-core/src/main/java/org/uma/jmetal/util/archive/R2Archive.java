//  R2Archive.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <juanjo.durillo@gmail.com>
//
//  Copyright (c) 2013 Antonio J. Nebro, Juan J. Durillo
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
import org.uma.jmetal.qualityIndicator.R2;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.EqualSolutions;
import org.uma.jmetal.util.random.PseudoRandom;

import java.util.Comparator;

/**
 * This class implements a bounded archive based on crowding distances (as
 * defined in NSGA-II).
 */
public class R2Archive extends Archive {

  /**
   *
   */
  private static final long serialVersionUID = -2344933516980116613L;

  /**
   * Stores the maximum size of the archive.
   */
  private int maxSize;

  /**
   * stores the number of the objectives.
   */
  private int objectives;

  /**
   * Stores a <code>Comparator</code> for dominance checking.
   */
  private Comparator<Solution> dominance;

  /**
   * Stores a <code>Comparator</code> for equality checking (in the objective
   * space).
   */
  private Comparator<Solution> equals;
  private Comparator<Solution> crowdingDistance;
  private R2 r2Indicator;

  /**
   * Constructor. Creates an R2Archive for a problem of 2 objectives
   *
   * @param maxSize The maximum size of the archive.
   */
  public R2Archive(int maxSize) {
    super(maxSize);
    this.maxSize = maxSize;
    objectives = 2;        // hardcoded
    dominance = new DominanceComparator();
    equals = new EqualSolutions();
    crowdingDistance = new CrowdingDistanceComparator();
    r2Indicator = new R2();
  }



  /**
   * Constructor.
   *
   * @param maxSize            The maximum size of the archive.
   * @param numberOfObjectives The number of objectives.
   */
  public R2Archive(int maxSize, int numberOfObjectives, String file) {
    super(maxSize);
    this.maxSize = maxSize;
    objectives = numberOfObjectives;
    dominance = new DominanceComparator();
    equals = new EqualSolutions();
    crowdingDistance = new CrowdingDistanceComparator();
    r2Indicator = new R2(numberOfObjectives, file);

  } // CrowdingArchive


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
   */
  public boolean add(Solution solution) {
    int flag = 0;
    int i = 0;
    Solution aux; //Store an solutiontype temporally

    while (i < solutionsList.size()) {
      aux = solutionsList.get(i);

      flag = dominance.compare(solution, aux);
      if (flag == 1) {               // The solutiontype to add is dominated
        return false;                // Discard the new solutiontype
      } else if (flag == -1) {       // A solutiontype in the archive is dominated
        solutionsList.remove(i);    // Remove it from the population
      } else {
        if (equals.compare(aux, solution) == 0) { // There is an equal solutiontype
          // in the population
          return false; // Discard the new solutiontype
        }  // if
        i++;
      }
    }
    // Insert the solutiontype into the archive
    solutionsList.add(solution);
    if (size() > maxSize) { // The archive is full
      // Removing the one contributing the less
      int indexWorst = this.r2Indicator.getWorst(this);
      remove(indexWorst);
    }
    return true;
  } // add


  /**
   * Returns a solutiontype from the archive based on their contribution to the R2
   * indicator. The solutiontype is chosen using a binary tournament.
   */
  public Solution
  getSolution() {
    int index1, index2;
    index1 = PseudoRandom.randInt(0, size() - 1);
    index2 = PseudoRandom.randInt(0, size() - 1);
    double aux1 = this.r2Indicator.R2Without(this, index1);
    double aux2 = this.r2Indicator.R2Without(this, index2);

    if (aux1 > aux2) { // means that index1 contributed less than index2
      return this.get(index1);
    } else {
      return this.get(index2);
    }
  }
}
