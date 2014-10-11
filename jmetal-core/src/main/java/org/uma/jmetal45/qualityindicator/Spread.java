//  Spread.java
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

package org.uma.jmetal45.qualityindicator;

import org.uma.jmetal45.qualityindicator.util.MetricsUtil;

import java.util.Arrays;

/**
 * This class implements the spread quality indicator.
 * It can be used also as command line program just by typing:
 * "java org.uma.jmetal45.qualityindicator.Spread <solutionFrontFile> <trueFrontFile>".
 * This metric is only applicable to two bi-objective problem.
 * Reference: Deb, K., Pratap, A., Agarwal, S., Meyarivan, T.: A fast and
 * elitist multiobjective genetic algorithm: NSGA-II. IEEE Trans.
 * on Evol. Computation 6 (2002) 182-197
 */
public class Spread implements QualityIndicator{
  private static final String NAME = "SPREAD" ;

  private static MetricsUtil utils;

  /**
   * Constructor.
   * Creates a new instance of a Spread object
   */
  public Spread() {
    utils = new MetricsUtil();
  }

  /**
   * Calculates the Spread metric. Given the front, the true pareto front as
   * <code>double []</code>, and the number of objectives,
   * the method returns the value of the metric.
   *
   * @param front              The front.
   * @param trueParetoFront    The true pareto front.
   */
  public double spread(double[][] front, double[][] trueParetoFront) {
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

    // STEP 3. Sort normalizedFront and normalizedParetoFront;
    Arrays.sort(normalizedFront,
      new org.uma.jmetal45.qualityindicator.util.LexicoGraphicalComparator());
    Arrays.sort(normalizedParetoFront,
      new org.uma.jmetal45.qualityindicator.util.LexicoGraphicalComparator());

    int numberOfPoints = normalizedFront.length;

    // STEP 4. Compute df and dl (See specifications in Deb's description of
    // the metric)
    double df = utils.distance(normalizedFront[0], normalizedParetoFront[0]);
    double dl = utils.distance(normalizedFront[normalizedFront.length - 1],
      normalizedParetoFront[normalizedParetoFront.length - 1]);

    double mean = 0.0;
    double diversitySum = df + dl;

    // STEP 5. Calculate the mean of distances between points i and (i - 1).
    // (the points are in lexicografical order)
    for (int i = 0; i < (normalizedFront.length - 1); i++) {
      mean += utils.distance(normalizedFront[i], normalizedFront[i + 1]);
    }

    mean = mean / (double) (numberOfPoints - 1);

    // STEP 6. If there are more than a single point, continue computing the
    // metric. In other case, return the worse value (1.0, see metric's
    // description).
    if (numberOfPoints > 1) {
      for (int i = 0; i < (numberOfPoints - 1); i++) {
        diversitySum += Math.abs(utils.distance(normalizedFront[i],
          normalizedFront[i + 1]) - mean);
      }
      return diversitySum / (df + dl + (numberOfPoints - 1) * mean);
    } else {
      return 1.0;
    }
  }

  @Override
  public double execute(double[][] paretoFrontApproximation, double[][] trueParetoFront) {
    return spread(paretoFrontApproximation, trueParetoFront);
  }

  @Override public String getName() {
    return NAME;
  }
}
