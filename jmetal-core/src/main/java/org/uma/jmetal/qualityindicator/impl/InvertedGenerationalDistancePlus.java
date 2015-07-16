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

package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.qualityindicator.NormalizableQualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.util.DominanceDistance;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * This class implements the inverted generational distance metric plust (IGD+)
 * Reference: Ishibuchi et al 2015, "A Study on Performance Evaluation Ability of a Modified
 * Inverted Generational Distance Indicator", GECCO 2015
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class InvertedGenerationalDistancePlus<Evaluate extends List<? extends Solution<?>>>
    extends NormalizableQualityIndicator<Evaluate, Double> {

  private Front referenceParetoFront ;

  /**
   *
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public InvertedGenerationalDistancePlus(String referenceParetoFrontFile) throws FileNotFoundException {
    super("IGD+", "Inverted generational distance plus") ;
    if (referenceParetoFrontFile == null) {
      throw new JMetalException("The pareto front approximation is null");
    }

    Front front = new ArrayFront(referenceParetoFrontFile);
    referenceParetoFront = front ;
    normalize = true ;
  }

  /**
   *
   * @param referenceParetoFront
   * @throws FileNotFoundException
   */
  public InvertedGenerationalDistancePlus(Front referenceParetoFront) {
    super("IGD+", "Inverted generational distance plus") ;
    if (referenceParetoFront == null) {
      throw new JMetalException("The pareto front approximation is null");
    }

    this.referenceParetoFront = referenceParetoFront ;
    normalize = true ;
  }

  /**
   * Evaluate method
   * @param solutionList
   * @return
   */
  @Override public Double evaluate(Evaluate solutionList) {
    if (solutionList == null) {
      throw new JMetalException("The pareto front approximation is null") ;
    }

    return invertedGenerationalDistancePlus(new ArrayFront(solutionList), referenceParetoFront);
  }

  /**
   * Returns the inverted generational distance value for a given front
   *
   * @param front The front
   * @param referenceFront The reference pareto front
   */
  public double invertedGenerationalDistancePlus(Front front, Front referenceFront) {
    Front normalizedFront;
    Front normalizedReferenceFront;

    if (normalize) {
      double[] maximumValue;
      double[] minimumValue;

      // STEP 1. Obtain the maximum and minimum values of the Pareto front
      maximumValue = FrontUtils.getMaximumValues(referenceFront);
      minimumValue = FrontUtils.getMinimumValues(referenceFront);

      // STEP 2. Get the normalized front and true Pareto fronts
      normalizedFront = FrontUtils.getNormalizedFront(front, maximumValue, minimumValue);
      normalizedReferenceFront =
          FrontUtils.getNormalizedFront(referenceFront, maximumValue, minimumValue);
    } else {
      normalizedFront = front ;
      normalizedReferenceFront = referenceFront ;
    }

    // STEP 3. Sum the distances between each point of the reference Pareto front and the dominated
    // region of the front
    double sum = 0.0;
    for (int i = 0 ; i < normalizedReferenceFront.getNumberOfPoints(); i++) {
        sum += FrontUtils.distanceToClosestPoint(normalizedReferenceFront.getPoint(i),
            normalizedFront, new DominanceDistance());
    }

    // STEP 4. Divide the sum by the maximum number of points of the reference Pareto front
    return sum / normalizedReferenceFront.getNumberOfPoints();
  }

  @Override
  public String getName() {
    return super.getName();
  }
}
