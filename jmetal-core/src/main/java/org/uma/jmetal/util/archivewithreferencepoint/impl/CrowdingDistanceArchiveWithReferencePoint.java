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

package org.uma.jmetal.util.archivewithreferencepoint.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Class representing a {@link ArchiveWithReferencePoint} archive using a crowding distance based density estimator
 * @author Antonio J. Nebro
 */
public class CrowdingDistanceArchiveWithReferencePoint<S extends Solution<?>> extends ArchiveWithReferencePoint<S> {
  private DensityEstimator<S> densityEstimator ;

  public CrowdingDistanceArchiveWithReferencePoint(int maxSize, List<Double> refPointDM) {
    super(maxSize, refPointDM, new CrowdingDistanceComparator<>());

    densityEstimator = new CrowdingDistance<>() ;
  }

  @Override
  public Comparator<S> getComparator() {
    return comparator;
  }

  @Override
  public void computeDensityEstimator() {
    densityEstimator.computeDensityEstimator(getSolutionList());
  }

  @Override
  public void sortByDensityEstimator() {
    Collections.sort(getSolutionList(), new CrowdingDistanceComparator<S>());
  }
}
