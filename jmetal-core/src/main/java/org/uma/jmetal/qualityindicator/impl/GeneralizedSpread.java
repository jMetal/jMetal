//  GeneralizedSpread.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
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

package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.imp.FrontUtils;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import org.uma.jmetal.util.point.impl.LexicographicalPointComparator;
import org.uma.jmetal.util.point.impl.PointDimensionComparator;
import org.uma.jmetal.util.point.impl.PointUtils;

import java.util.List;

/**
 * This class implements the generalized spread metric for two or more dimensions.
 * Reference: A. Zhou, Y. Jin, Q. Zhang, B. Sendhoff, and E. Tsang
 * Combining model-based and genetics-based offspring generation for
 * multi-objective optimization using a convergence criterion,
 * 2006 IEEE Congress on Evolutionary Computation, 2006, pp. 3234-3241.
 */
public class GeneralizedSpread implements QualityIndicator {
  private static final String NAME = "GSPREAD" ;

  /**
   * Constructor
   * Creates a new instance of GeneralizedSpread
   */
  public GeneralizedSpread() {
  }

  @Override
  public double execute(Front paretoFrontApproximation, Front trueParetoFront) {
    if (paretoFrontApproximation == null) {
      throw new JMetalException("The pareto front approximation object is null") ;
    } else if (trueParetoFront == null) {
      throw new JMetalException("The pareto front object is null");
    }

    return generalizedSpread(paretoFrontApproximation, trueParetoFront);
  }

  @Override
  public double execute(List<? extends Solution> paretoFrontApproximation,
      List<? extends Solution> trueParetoFront) {

    if (paretoFrontApproximation == null) {
      throw new JMetalException("The pareto front approximation object is null") ;
    } else if (trueParetoFront == null) {
      throw new JMetalException("The pareto front object is null");
    }

    return this.execute(new ArrayFront(paretoFrontApproximation), new ArrayFront(trueParetoFront)) ;
  }

  /**
   *  Calculates the generalized spread metric. Given the 
   *  pareto front, the true pareto front as <code>double []</code>
   *  and the number of objectives, the method return the value for the
   *  metric.
   *  @param paretoFront The pareto front.
   *  @param paretoTrueFront The true pareto front.
   *  @return the value of the generalized spread metric
   **/
  public double generalizedSpread(Front paretoFront, Front paretoTrueFront) {
    double [] maximumValue;
    double [] minimumValue;
    Front normalizedFront;
    Front normalizedParetoFront;

    int numberOfObjectives = paretoFront.getPoint(0).getNumberOfDimensions() ;

    // STEP 1. Obtain the maximum and minimum values of the Pareto front
    maximumValue = FrontUtils.getMaximumValues(paretoTrueFront);
    minimumValue = FrontUtils.getMinimumValues(paretoTrueFront);

    normalizedFront = FrontUtils.getNormalizedFront(paretoFront,
        maximumValue,
        minimumValue);

    // STEP 2. Get the normalized front and true Pareto fronts
    normalizedParetoFront = FrontUtils.getNormalizedFront(paretoTrueFront,
        maximumValue,
        minimumValue);

    // STEP 3. Find extremal values

    Point[] extremeValues = new Point[numberOfObjectives] ;
    for (int i = 0; i < numberOfObjectives; i++) {
      normalizedParetoFront.sort(new PointDimensionComparator(i));
      Point newPoint = new ArrayPoint(numberOfObjectives) ;
      for (int j = 0 ; j < numberOfObjectives; j++) {
        newPoint.setDimensionValue(j,
            normalizedParetoFront.getPoint(normalizedParetoFront.getNumberOfPoints()-1).getDimensionValue(j));
      }
      extremeValues[i] = newPoint ;
    }

    int numberOfPoints     = normalizedFront.getNumberOfPoints();

    // STEP 4. Sorts the normalized front
    normalizedFront.sort(new LexicographicalPointComparator());

    // STEP 5. Calculate the metric value. The value is 1.0 by default
    if (PointUtils.euclideanDistance(normalizedFront.getPoint(0),
        normalizedFront.getPoint(normalizedFront.getNumberOfPoints() - 1)) == 0.0) {
      return 1.0;
    } else {
      double dmean = 0.0;

      // STEP 6. Calculate the mean distance between each point and its nearest neighbor
      for (int i = 0 ; i < normalizedFront.getNumberOfPoints(); i++) {
        dmean += FrontUtils.distanceToNearestPoint(normalizedFront.getPoint(i), normalizedFront);
      }

      dmean = dmean / (numberOfPoints);

      // STEP 7. Calculate the distance to extremal values
      double dExtrems = 0.0;
      for (int i = 0 ; i < extremeValues.length; i++) {
        dExtrems += FrontUtils.distanceToClosestPoint(extremeValues[i], normalizedFront);
      }

      // STEP 8. Computing the value of the metric
      double mean = 0.0;
      for (int i = 0; i < normalizedFront.getNumberOfPoints(); i++) {
        mean += Math.abs(FrontUtils.distanceToNearestPoint(normalizedFront.getPoint(i), normalizedFront) -
            dmean);
      }

      return (dExtrems + mean) / (dExtrems + (numberOfPoints*dmean));      
    }
  }



  @Override
  public String getName() {
    return NAME;
  }
}
