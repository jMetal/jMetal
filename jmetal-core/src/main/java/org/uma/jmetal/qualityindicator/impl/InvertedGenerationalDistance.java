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

import java.io.FileNotFoundException;
import java.util.List;

/**
 * This class implements the inverted generational distance metric.
 * Reference: Van Veldhuizen, D.A., Lamont, G.B.: Multiobjective Evolutionary Algorithm Research:
 * A History and Analysis.
 * Technical Report TR-98-03, Dept. Elec. Comput. Eng., Air Force
 * Inst. Technol. (1998)
 */
public class InvertedGenerationalDistance
    extends NormalizableQualityIndicator<List<? extends Solution<?>>, Double> {
  private static final double POW = 2.0;

  private Front referenceParetoFront ;

  /**
   *
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public InvertedGenerationalDistance(String referenceParetoFrontFile) throws FileNotFoundException {
    super("IGD", "Inverted generational distance") ;
    if (referenceParetoFrontFile == null) {
      throw new JMetalException("The pareto front object is null");
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
  public InvertedGenerationalDistance(Front referenceParetoFront) {
    super("IGD", "Inverted generational distance") ;
    if (referenceParetoFront == null) {
      throw new JMetalException("The pareto front is null");
    }

    this.referenceParetoFront = referenceParetoFront ;
    this.normalize = true ;
  }

  /**
   * Evaluate method
   * @param solutionList
   * @return
   */
  @Override public Double evaluate(List<? extends Solution<?>> solutionList) {
    return invertedGenerationalDistance(new ArrayFront(solutionList), referenceParetoFront);
  }

  /**
   * Returns the inverted generational distance value for a given front
   *
   * @param front           The front
   * @param trueParetoFront The true pareto front
   */
  public double invertedGenerationalDistance(Front front, Front trueParetoFront) {
    Front normalizedFront;
    Front normalizedParetoFront;

    if (normalize) {
      double[] maximumValue;
      double[] minimumValue;

      // STEP 1. Obtain the maximum and minimum values of the Pareto front
      maximumValue = FrontUtils.getMaximumValues(trueParetoFront);
      minimumValue = FrontUtils.getMinimumValues(trueParetoFront);

      // STEP 2. Get the normalized front and true Pareto fronts
      normalizedFront = FrontUtils.getNormalizedFront(front, maximumValue, minimumValue);
      normalizedParetoFront =
          FrontUtils.getNormalizedFront(trueParetoFront, maximumValue, minimumValue);
    } else {
      normalizedFront = front ;
      normalizedParetoFront = trueParetoFront ;
    }

    // STEP 3. Sum the distances between each point of the true Pareto front and
    // the nearest point in the true Pareto front
    double sum = 0.0;
    for (int i = 0 ; i < normalizedParetoFront.getNumberOfPoints(); i++) {
      sum += Math.pow(FrontUtils.distanceToClosestPoint(normalizedParetoFront.getPoint(i),
          normalizedFront), POW);
    }

    // STEP 4. Obtain the sqrt of the sum
    sum = Math.pow(sum, 1.0 / POW);

    // STEP 5. Divide the sum by the maximum number of points of the reference Pareto front
    return sum / normalizedParetoFront.getNumberOfPoints();
  }

  @Override
  public String getName() {
    return super.getName();
  }
}
