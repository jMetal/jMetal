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

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.naming.impl.SimpleDescribedEntity;
import org.uma.jmetal.util.point.Point;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * This class implements the inverted generational distance metric plust (IGD+)
 * Reference: Ishibuchi et al 2015, "A Study on Performance Evaluation Ability of a Modified
 * Inverted Generational Distance Indicator", GECCO 2015
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class InvertedGenerationalDistancePlus extends SimpleDescribedEntity
    implements QualityIndicator<List<? extends Solution<?>>, Double>  {

  private Front referenceParetoFront ;

  /**
   *
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public InvertedGenerationalDistancePlus(String referenceParetoFrontFile) throws FileNotFoundException {
    super("IGD", "Inverted generational distance") ;
    if (referenceParetoFrontFile == null) {
      throw new JMetalException("The reference pareto front is null");
    }

    Front front = new ArrayFront(referenceParetoFrontFile);
    referenceParetoFront = front ;
  }

  /**
   *
   * @param referenceParetoFront
   * @throws FileNotFoundException
   */
  public InvertedGenerationalDistancePlus(Front referenceParetoFront) {
    super("IGD", "Inverted generational distance") ;
    if (referenceParetoFront == null) {
      throw new JMetalException("The pareto front is null");
    }

    this.referenceParetoFront = referenceParetoFront ;
  }

  @Override public Double evaluate(List<? extends Solution<?>> solutionList) {
    return invertedGenerationalDistance(new ArrayFront(solutionList), referenceParetoFront);
  }

  /**
   * Returns the inverted generational distance value for a given front
   *
   * @param front The front
   * @param referenceFront The reference pareto front
   */
  public double invertedGenerationalDistance(Front front, Front referenceFront) {
    double[] maximumValue;
    double[] minimumValue;
    Front normalizedFront;
    Front normalizedReferenceFront;

    // STEP 1. Obtain the maximum and minimum values of the reference Pareto front
    maximumValue = FrontUtils.getMaximumValues(referenceFront);
    minimumValue = FrontUtils.getMinimumValues(referenceFront);

    // STEP 2. Get the normalized front and normalized reference Pareto front
    normalizedFront = FrontUtils.getNormalizedFront(front, maximumValue, minimumValue);
    normalizedReferenceFront = FrontUtils.getNormalizedFront(referenceFront,
      maximumValue,
      minimumValue);

    // STEP 3. Sum the distances between each point of the reference Pareto front and the dominated
    // region of the front
    double sum = 0.0;
    for (int i = 0 ; i < normalizedReferenceFront.getNumberOfPoints(); i++) {
        sum += distanceToClosestPoint(normalizedReferenceFront.getPoint(i), normalizedFront);
    }

    // STEP 4. Divide the sum by the maximum number of points of the reference Pareto front
    return sum / normalizedReferenceFront.getNumberOfPoints();
  }

  @Override
  public String getName() {
    return super.getName();
  }

  /**
   * Calculate distance of the reference point to the point of the front point according to
   * Pareto dominance. As jMetal minimizes by default, the expression (zk - ak) of the reference
   * paper is changed by (ak - zk). Note: a is the point, z is the reference point
   * @param point
   * @param referencePoint
   * @return
   */
  private double calculateDistanceBetweenPoints(Point point, Point referencePoint) {
    double result = 0.0 ;
    for (int i = 0; i < point.getNumberOfDimensions(); i++) {
      double value = point.getDimensionValue(i) - referencePoint.getDimensionValue(i) ;
      if (value > 0.0) {
        result += Math.pow(value, 2.0) ;
      }
    }

    return Math.sqrt(result) ;
  }

  /**
   * Calculates the minimum distance from a point of the reference Pareto front to the front
   * @param point
   * @param front
   * @return
   */
  private double distanceToClosestPoint(Point point, Front front) {
    double minDistance = calculateDistanceBetweenPoints(point, front.getPoint(0));

    for (int i = 1; i < front.getNumberOfPoints(); i++) {
      double aux = calculateDistanceBetweenPoints(point, front.getPoint(i));
      if (aux < minDistance) {
        minDistance = aux;
      }
    }

    return minDistance;
  }
}
