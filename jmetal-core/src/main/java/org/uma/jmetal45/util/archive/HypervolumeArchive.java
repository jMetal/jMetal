//  HypervolumeArchive.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
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

package org.uma.jmetal45.util.archive;

import org.uma.jmetal45.core.Solution;
import org.uma.jmetal45.qualityindicator.util.MetricsUtil;
import org.uma.jmetal45.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal45.util.comparator.DominanceComparator;
import org.uma.jmetal45.util.comparator.EqualSolutions;

import java.util.Comparator;

/**
 * This class implements a bounded setArchive based on the Hypervolume quality indicator).
 */
public class HypervolumeArchive extends Archive {
  private static final long serialVersionUID = 3084983453913397884L;
  private int maxSize;
  private int numberOfObjectives;
  private Comparator<Solution> dominanceComparator;
  private Comparator<Solution> equalsComparator;

  private MetricsUtil utils;

  private double offset;

  /**
   * Constructor.
   *
   * @param maxSize            The maximum size of the setArchive.
   * @param numberOfObjectives The number of numberOfObjectives.
   */
  public HypervolumeArchive(int maxSize, int numberOfObjectives) {
    super(maxSize);
    this.maxSize = maxSize;
    this.numberOfObjectives = numberOfObjectives;
    dominanceComparator = new DominanceComparator();
    equalsComparator = new EqualSolutions();
    utils = new MetricsUtil();
    offset = 100;
  } 

  /**
   * Adds a <code>Solution</code> to the setArchive. If the <code>Solution</code>
   * is dominated by any member of the setArchive, then it is discarded. If the
   * <code>Solution</code> dominates some members of the setArchive, these are
   * removed. If the setArchive is full and the <code>Solution</code> has to be
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
    // Insert the solution into the setArchive
    solutionsList.add(solution);
    if (size() > maxSize) { // The setArchive is full
      double[][] frontValues = this.writeObjectivesToMatrix();
      int numberOfObjectives = this.numberOfObjectives;
      // STEP 1. Obtain the maximum and minimum values of the Pareto front
      double[] maximumValues =
        utils.getMaximumValues(this.writeObjectivesToMatrix(), numberOfObjectives);
      double[] minimumValues =
        utils.getMinimumValues(this.writeObjectivesToMatrix(), numberOfObjectives);
      // STEP 2. Get the normalized front
      double[][] normalizedFront =
        utils.getNormalizedFront(frontValues, maximumValues, minimumValues);
      // compute offsets for reference point in normalized space
      double[] offsets = new double[maximumValues.length];
      for (i = 0; i < maximumValues.length; i++) {
        offsets[i] = offset / (maximumValues[i] - minimumValues[i]);
      }
      // STEP 3. Inverse the pareto front. This is needed because the original
      //metric by Zitzler is for maximization problem
      double[][] invertedFront = utils.invertedFront(normalizedFront);
      // shift away from origin, so that boundary points also get a contribution > 0
      for (double[] point : invertedFront) {
        for (i = 0; i < point.length; i++) {
          point[i] += offsets[i];
        }
      }

      // calculate contributions and sort
      double[] contributions = utils.hvContributions(this.numberOfObjectives, invertedFront);
      for (i = 0; i < contributions.length; i++) {
        // contribution values are used analogously to crowding distance
        this.get(i).setCrowdingDistance(contributions[i]);
      }

      this.sort(new CrowdingDistanceComparator());

      remove(size() - 1);
    }
    return true;
  }

  /**
   * This method forces to compute the contribution of each solution
   */
  public void actualiseHVContribution() {
    if (size() > 2) { // The contribution can be updated
      double[][] frontValues = this.writeObjectivesToMatrix();
      int numberOfObjectives = this.numberOfObjectives;
      // STEP 1. Obtain the maximum and minimum values of the Pareto front
      double[] maximumValues =
        utils.getMaximumValues(this.writeObjectivesToMatrix(), numberOfObjectives);
      double[] minimumValues =
        utils.getMinimumValues(this.writeObjectivesToMatrix(), numberOfObjectives);
      // STEP 2. Get the normalized front
      double[][] normalizedFront =
        utils.getNormalizedFront(frontValues, maximumValues, minimumValues);
      // compute offsets for reference point in normalized space
      double[] offsets = new double[maximumValues.length];
      for (int i = 0; i < maximumValues.length; i++) {
        offsets[i] = offset / (maximumValues[i] - minimumValues[i]);
      }
      // STEP 3. Inverse the pareto front. This is needed because the original
      //metric by Zitzler is for maximization problem
      double[][] invertedFront = utils.invertedFront(normalizedFront);
      // shift away from origin, so that boundary points also get a contribution > 0
      for (double[] point : invertedFront) {
        for (int i = 0; i < point.length; i++) {
          point[i] += offsets[i];
        }
      }

      // calculate contributions and sort
      double[] contributions = utils.hvContributions(this.numberOfObjectives, invertedFront);
      for (int i = 0; i < contributions.length; i++) {
        // contribution values are used analogously to crowding distance
        this.get(i).setCrowdingDistance(contributions[i]);
      }
    }
  }

  /**
   * This method returns the location (integer position) of a solution in the setArchive.
   * For that, the equalsComparator comparator is used
   */
  public int getLocation(Solution solution) {
    int location = -1;
    int index = 0;
    while ((index < size()) && (location != -1)) {
      if (equalsComparator.compare(solution, get(index)) == 0) {
        location = index;
      }
      index++;
    }
    return location;
  }
}
