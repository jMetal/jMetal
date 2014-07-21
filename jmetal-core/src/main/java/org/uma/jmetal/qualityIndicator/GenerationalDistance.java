//  GenerationalDistance.java
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

/**
 * This class implements the generational distance indicator. It can be used also
 * as a command line by typing:
 * "java org.uma.jmetal.qualityIndicator.GenerationalDistance <solutionFrontFile>
 * <trueFrontFile> <getNumberOfObjectives>"
 * Reference: Van Veldhuizen, D.A., Lamont, G.B.: Multiobjective Evolutionary
 * Algorithm Research: A History and Analysis.
 * Technical Report TR-98-03, Dept. Elec. Comput. Eng., Air Force
 * Inst. Technol. (1998)
 */
public class GenerationalDistance implements QualityIndicator {
  private static final String NAME = "GD" ;

  private static final double pow = 2.0;
  private MetricsUtil utils;

  /**
   * Constructor.
   * Creates a new instance of the generational distance metric.
   */
  public GenerationalDistance() {
    utils = new MetricsUtil();
  }

  /**
   * Returns the generational distance value for a given front
   *
   * @param front           The front
   * @param trueParetoFront The true pareto front
   */
  public double generationalDistance(double[][] front, double[][] trueParetoFront) {
    double[] maximumValue;
    double[] minimumValue;
    double[][] normalizedFront;
    double[][] normalizedParetoFront;

    int numberOfObjectives = trueParetoFront[0].length ;

    // STEP 1. Obtain the maximum and minimum values of the Pareto front
    maximumValue = utils.getMaximumValues(trueParetoFront, numberOfObjectives);
    minimumValue = utils.getMinimumValues(trueParetoFront, numberOfObjectives);

    // STEP 2. Get the normalized front and true Pareto fronts
    normalizedFront = utils.getNormalizedFront(front,
      maximumValue,
      minimumValue);
    normalizedParetoFront = utils.getNormalizedFront(trueParetoFront,
      maximumValue,
      minimumValue);

    // STEP 3. Sum the distances between each point of the front and the
    // nearest point in the true Pareto front
    double sum = 0.0;
    for (int i = 0; i < front.length; i++) {
      sum += Math.pow(utils.distanceToClosedPoint(normalizedFront[i],
          normalizedParetoFront),
        pow
      );
    }


    // STEP 4. Obtain the sqrt of the sum
    sum = Math.pow(sum, 1.0 / pow);

    // STEP 5. Divide the sum by the maximum number of points of the front
    double generationalDistance = sum / normalizedFront.length;

    return generationalDistance;
  }

  @Override
  public double execute(double[][] paretoFrontApproximation, double[][] paretoTrueFront) {
    return generationalDistance(paretoFrontApproximation, paretoTrueFront) ;
  }

  @Override public String getName() {
    return NAME;
  }
}
