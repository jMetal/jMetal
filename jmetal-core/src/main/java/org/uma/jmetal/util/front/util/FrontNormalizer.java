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

package org.uma.jmetal.util.front.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;

import java.util.List;

/**
 * Class for normalizing fronts
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class FrontNormalizer {

  private double[] maximumValues;
  private double[] minimumValues;

  /**
   * Constructor.
   * @param referenceFront
   */
  public FrontNormalizer(List<? extends Solution<?>> referenceFront) {
    maximumValues = FrontUtils.getMaximumValues(new ArrayFront(referenceFront));
    minimumValues = FrontUtils.getMinimumValues(new ArrayFront(referenceFront));
  }

  public FrontNormalizer(double[] minimumValues, double[] maximumValues) {
    this.maximumValues = maximumValues ;
    this.minimumValues = minimumValues ;
  }

  /**
   * Returns a normalized front
   * @param solutionList
   * @return
   */
  public List<? extends Solution<?>> normalize(List<? extends Solution<?>> solutionList) {
    // STEP 2. Get the normalized front and true Pareto fronts
    Front normalizedFront ;
    normalizedFront = FrontUtils.getNormalizedFront(new ArrayFront(solutionList), maximumValues, minimumValues);

    return FrontUtils.convertFrontToSolutionList(normalizedFront) ;
  }
}
