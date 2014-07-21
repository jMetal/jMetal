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

package org.uma.jmetal.qualityIndicator;

import org.uma.jmetal.qualityIndicator.util.MetricsUtil;

import java.util.Arrays;

/**
 * This class implements the generalized spread metric for two or more dimensions.
 * It can be used also as command line program just by typing.
 * $ java org.uma.jmetal.qualityIndicator.GeneralizedSpread <solutionFrontFile> <trueFrontFile> <getNumberOfObjectives>
 * Reference: A. Zhou, Y. Jin, Q. Zhang, B. Sendhoff, and E. Tsang
 * Combining model-based and genetics-based offspring generation for
 * multi-objective optimization using a convergence criterion,
 * 2006 IEEE Congress on Evolutionary Computation, 2006, pp. 3234-3241.
 */
public class GeneralizedSpread implements QualityIndicator {
  private static final String NAME = "GSPREAD" ;

  private static MetricsUtil utils;

  /**
   * Constructor
   * Creates a new instance of GeneralizedSpread
   */
  public GeneralizedSpread() {
    utils = new MetricsUtil();
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
  public double generalizedSpread(double [][] paretoFront, double [][] paretoTrueFront) {
    double [] maximumValue;
    double [] minimumValue;
    double [][] normalizedFront;
    double[][] normalizedParetoFront;

    int numberOfObjectives = paretoFront[0].length ;

    // STEP 1. Obtain the maximum and minimum values of the Pareto front
    maximumValue = utils.getMaximumValues(paretoTrueFront,numberOfObjectives);
    minimumValue = utils.getMinimumValues(paretoTrueFront,numberOfObjectives);

    normalizedFront = utils.getNormalizedFront(paretoFront,
        maximumValue,
        minimumValue);

    // STEP 2. Get the normalized front and true Pareto fronts
    normalizedParetoFront = utils.getNormalizedFront(paretoTrueFront,
        maximumValue,
        minimumValue);

    // STEP 3. Find extremal values
    double[][] extremValues = new double[numberOfObjectives][numberOfObjectives];
    for (int i = 0; i < numberOfObjectives; i++) {
      Arrays.sort(normalizedParetoFront,new org.uma.jmetal.qualityIndicator.util.ValueComparator(i));
      System.arraycopy(normalizedParetoFront[normalizedParetoFront.length - 1], 0, extremValues[i], 0, numberOfObjectives);
    }

    int numberOfPoints     = normalizedFront.length;
    int numberOfTruePoints = normalizedParetoFront.length;

    // STEP 4. Sorts the normalized front
    Arrays.sort(normalizedFront, new org.uma.jmetal.qualityIndicator.util.LexicoGraphicalComparator());

    // STEP 5. Calculate the metric value. The value is 1.0 by default
    if (utils.distance(normalizedFront[0], normalizedFront[normalizedFront.length - 1]) == 0.0) {
      return 1.0;
    } else {

      double dmean = 0.0;

      // STEP 6. Calculate the mean distance between each point and its nearest neighbor
      for (double[] aNormalizedFront : normalizedFront) {
        dmean += utils.distanceToNearestPoint(aNormalizedFront, normalizedFront);
      }

      dmean = dmean / (numberOfPoints);

      // STEP 7. Calculate the distance to extremal values
      double dExtrems = 0.0;
      for (double[] extremValue : extremValues) {
        dExtrems += utils.distanceToClosedPoint(extremValue, normalizedFront);
      }

      // STEP 8. Computing the value of the metric
      double mean = 0.0;
      for (double[] aNormalizedFront : normalizedFront) {
        mean += Math.abs(utils.distanceToNearestPoint(aNormalizedFront, normalizedFront) -
            dmean);
      }

      return (dExtrems + mean) / (dExtrems + (numberOfPoints*dmean));      
    }
  }

  @Override
  public double execute(double[][] paretoFrontApproximation, double[][] paretoTrueFront) {
    return generalizedSpread(paretoFrontApproximation, paretoTrueFront);
  }

  @Override public String getName() {
    return null;
  }
}
