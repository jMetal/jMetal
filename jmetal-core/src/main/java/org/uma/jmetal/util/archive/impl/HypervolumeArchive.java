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
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.solutionattribute.DensityEstimator;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by Antonio J. Nebro on 24/09/14.
 */
public class HypervolumeArchive<S extends Solution<?>> extends AbstractBoundedArchive<S> {
  private Comparator<S> dominanceComparator;

  public HypervolumeArchive(int maxSize) {
    super(maxSize);
    dominanceComparator = new CrowdingDistanceComparator<S>() ;
  }

  @Override
  public void prune() {


  }
}
