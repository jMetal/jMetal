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

package org.uma.jmetal.newideas.cosine.rsmpso.archive.archivewithreferencepoint.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.newideas.cosine.rsmpso.archive.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.WFGHypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.HypervolumeContributionComparator;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;

/**
 * Created by Antonio J. Nebro on 24/09/14.
 */
public class HypervolumeArchiveWithReferencePoint<S extends Solution<?>> extends ArchiveWithReferencePoint<S> {
  private Hypervolume<S> hypervolume ;

  public HypervolumeArchiveWithReferencePoint(int maxSize, List<Double> refPointDM) {
    super(maxSize, refPointDM, new HypervolumeContributionComparator<>());

    hypervolume = new PISAHypervolume<>() ;
  }

  @Override
  public Comparator<S> getComparator() {
    return comparator;
  }

  @Override
  public void computeDensityEstimator() {
    if (archive.size() > 3) {
      hypervolume
          .computeHypervolumeContribution(archive.getSolutionList(), archive.getSolutionList());
    }
  }

  @Override
  public void sortByDensityEstimator() {
    Collections.sort(getSolutionList(), new HypervolumeContributionComparator<S>());
  }
}
