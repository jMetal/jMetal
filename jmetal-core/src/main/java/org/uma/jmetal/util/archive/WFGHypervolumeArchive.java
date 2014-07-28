//  HypervolumeArchive.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo<juanjod@gmail.com>
//
//  Copyright (c) 2013 Antonio J. Nebro
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
import org.uma.jmetal.qualityIndicator.fastHypervolume.wfg.Point;
import org.uma.jmetal.qualityIndicator.fastHypervolume.wfg.WFGHV;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.EqualSolutions;

import java.util.Comparator;

/**
 * This class implements a bounded archive based on the Hypervolume quality indicator (wfg implementation)
 */
public class WFGHypervolumeArchive extends Archive {
  private static final long serialVersionUID = 2607976470731454148L;

  private int maxSize;
  private int numberOfObjectives;
  private Comparator<Solution> dominanceComparator;
  private Comparator<Solution> equalsComparator;

  private WFGHV wfg = null;

  /**
   * Constructor.
   *
   * @param maxSize            The maximum size of the archive.
   * @param numberOfObjectives The number of numberOfObjectives.
   */
  public WFGHypervolumeArchive(int maxSize, int numberOfObjectives) {
    super(maxSize);
    this.maxSize = maxSize;
    this.numberOfObjectives = numberOfObjectives;
    dominanceComparator = new DominanceComparator();
    equalsComparator = new EqualSolutions() ;
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
   */
  public boolean add(Solution solution) {
    int flag ;
    int i = 0;
    Solution aux;

    while (i < solutionsList.size()) {
      aux = solutionsList.get(i);

      flag = dominanceComparator.compare(solution, aux);
      if (flag == 1) {               // The solutiontype to add is dominated
        return false;                // Discard the new solutiontype
      } else if (flag == -1) {       // A solutiontype in the archive is dominated
        solutionsList.remove(i);    // Remove it from the population
      } else {
        if (equalsComparator.compare(aux, solution) == 0) { // There is an equal solutiontype
          // in the population
          return false; // Discard the new solutiontype
        }  // if
        i++;
      }
    }
    // Insert the solution into the archive
    solutionsList.add(solution);
    if (size() > maxSize) { // The archive is full

      // computing the reference point
      double[] vector = new double[numberOfObjectives];
      for (int o = 0; o < numberOfObjectives; o++) {
        vector[0] = this.get(0).getObjective(o);
      }
      for (int j = 1; j < this.size(); j++) {
        for (int o = 0; o < numberOfObjectives; o++) {
          if (this.get(j).getObjective(o) > vector[o]) {
            vector[o] = 1.0;//this.get(j).getObjective(o);
          }
        }
      }
      Point p = new Point(vector);
      wfg = new WFGHV(this.numberOfObjectives, this.size(), p);
      //remove(indexWorst(crowdingDistance_));

      remove(wfg.getLessContributorHV(this));
    }
    return true;
  }


  public void computeHVContribution() {
    // computing the reference point
    double[] vector = new double[numberOfObjectives];
    for (int o = 0; o < numberOfObjectives; o++) {
      vector[0] = this.get(0).getObjective(o);
    }
    for (int j = 1; j < this.size(); j++) {
      for (int o = 0; o < numberOfObjectives; o++) {
        if (this.get(j).getObjective(o) > vector[o]) {
          vector[o] = 1.0;//this.get(j).getObjective(o);
        }
      }
    }
    Point p = new Point(vector);
    wfg = new WFGHV(this.numberOfObjectives, this.size(), p);
    wfg.getLessContributorHV(this);
  }
}
