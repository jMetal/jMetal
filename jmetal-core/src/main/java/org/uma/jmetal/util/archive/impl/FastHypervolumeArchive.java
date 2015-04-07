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
public class FastHypervolumeArchive <S extends Solution> extends AbstractBoundedArchive<S> {

  public Point referencePoint;


  private Comparator<Solution> hvContributionComparator;

  /**
   * Constructor.
   *
   * @param maxSize            The maximum size of the setArchive.
   * @param numberOfObjectives The number of objectives.
   */
  public FastHypervolumeArchive(int maxSize, int numberOfObjectives) {
    super(maxSize);
    referencePoint = new ArrayPoint(numberOfObjectives);
    for (int i = 0; i < numberOfObjectives; i++) {
      referencePoint.setDimensionValue(i, Double.MAX_VALUE);
    }
    hvContributionComparator = new HypervolumeContributorComparator();
  }


  /**
   * This method forces to compute the contribution of each solutiontype (required for PAEShv)
   */
  private void computeHVContribution() {
    //if (size() > 2) { // The contribution can be updated

    FastHypervolume fastHV = new FastHypervolume();
    fastHV.computeHVContributions(getSolutionList());
    //}
  }

  @Override
  public void prune() {
	if (getSolutionList().size() > getMaxSize()) {
      computeHVContribution();
      getSolutionList().remove(SolutionListUtils.findIndexOfWorstSolution(getSolutionList(), hvContributionComparator));
    }
  }
}
