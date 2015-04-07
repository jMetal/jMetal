//  FastHypervolumeArchive.java
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

package org.uma.jmetal.util.archive.impl;

import org.uma.jmetal.qualityindicator.util.FastHypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.HypervolumeContributorComparator;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This class implements a bounded archive based on the hypervolume quality indicator
 */
public class FastHypervolumeArchive <S extends Solution> implements BoundedArchive<S> {
  private List<S> solutionList;

  public Point referencePoint;
  private int maxSize;

  private Comparator<Solution> dominanceComparator;
  private Comparator<Solution> equalsComparator;

  private Comparator<Solution> hvContributionComparator;

  /**
   * Constructor.
   *
   * @param maxSize            The maximum size of the setArchive.
   * @param numberOfObjectives The number of objectives.
   */
  public FastHypervolumeArchive(int maxSize, int numberOfObjectives) {
    this.maxSize = maxSize;
    solutionList = new ArrayList<>(maxSize);
    dominanceComparator = new DominanceComparator();
    //equalsComparator = new EqualSolutions();

    referencePoint = new ArrayPoint(numberOfObjectives);
    for (int i = 0; i < numberOfObjectives; i++) {
      referencePoint.setDimensionValue(i, Double.MAX_VALUE);
    }

    hvContributionComparator = new HypervolumeContributorComparator();
  }

  /**
   * Adds a <code>Solution</code> to the setArchive. If the <code>Solution</code>
   * is dominated by any member of the setArchive, then it is discarded. If the
   * <code>Solution</code> dominates some members of the setArchive, these are
   * removed. If the setArchive is full and the <code>Solution</code> has to be
   * inserted, the solutiontype contributing the least to the HV of the solutiontype set
   * is discarded.
   *
   * @param solution The <code>Solution</code>
   * @return true if the <code>Solution</code> has been inserted, false
   * otherwise.
   */
  public boolean add(S solution) {
    int flag;
    Solution auxiliarSolution;

    int i = 0;
    while (i < solutionList.size()) {
      auxiliarSolution = solutionList.get(i);

      flag = dominanceComparator.compare(solution, auxiliarSolution);
      if (flag == 1) {
        return false;
      } else if (flag == -1) {
        solutionList.remove(i);
      } else {
        //if (equalsComparator.compare(auxiliarSolution, solution) == 0) { // There is an equal solutiontype
        // in the population
        //  return false; // Discard the new solutiontype
        //}
        i++;
      }
    }
    solutionList.add(solution);
    if (solutionList.size() > maxSize) {
      computeHVContribution();

      solutionList.remove(SolutionListUtils.findIndexOfWorstSolution(solutionList, hvContributionComparator));
    }
    return true;
  }


  /**
   * This method forces to compute the contribution of each solutiontype (required for PAEShv)
   */
  public void computeHVContribution() {
    //if (size() > 2) { // The contribution can be updated

    FastHypervolume fastHV = new FastHypervolume();
    fastHV.computeHVContributions(solutionList);
    //}
  }

  @Override
  public List<S> getSolutionList() {
    return solutionList;
  }

  @Override public int size() {
    return solutionList.size();
  }

  @Override
  public int getMaxSize() {
    return maxSize ;
  }

  @Override public S get(int index) {
    return solutionList.get(index);
  }
}
