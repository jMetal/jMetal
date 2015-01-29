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

package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.imp.FrontUtils;
import org.uma.jmetal.util.point.impl.LexicographicalPointComparator;
import org.uma.jmetal.util.point.impl.PointUtils;

import java.util.List;

/**
 * This class implements the spread quality indicator. It must be only to two bi-objective problem.
 * Reference: Deb, K., Pratap, A., Agarwal, S., Meyarivan, T.: A fast and
 * elitist multiobjective genetic algorithm: NSGA-II. IEEE Trans. on Evol. Computation 6 (2002) 182-197
 */
public class Spread implements QualityIndicator {
  private static final String NAME = "SPREAD" ;

  /**
   * Constructor.
   * Creates a new instance of a Spread object
   */
  public Spread() {
  }

  @Override
  public double execute(Front paretoFrontApproximation, Front trueParetoFront) {
    if (paretoFrontApproximation == null) {
      throw new JMetalException("The pareto front approximation object is null") ;
    } else if (trueParetoFront == null) {
      throw new JMetalException("The pareto front object is null");
    }

    return spread(paretoFrontApproximation, trueParetoFront);
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
   * Calculates the Spread metric.
   *
   * @param front              The front.
   * @param trueParetoFront    The true pareto front.
   */
  public double spread(Front front, Front trueParetoFront) {
    double[] maximumValue;
    double[] minimumValue;
    Front normalizedFront;
    Front normalizedParetoFront;

    // STEP 1. Obtain the maximum and minimum values of the Pareto front
    maximumValue = FrontUtils.getMaximumValues(trueParetoFront);
    minimumValue = FrontUtils.getMinimumValues(trueParetoFront);

    // STEP 2. Get the normalized front and true Pareto fronts
    normalizedFront = FrontUtils.getNormalizedFront(front,
      maximumValue,
      minimumValue);
    normalizedParetoFront = FrontUtils.getNormalizedFront(trueParetoFront,
      maximumValue,
      minimumValue);

    // STEP 3. Sort normalizedFront and normalizedParetoFront;
    normalizedFront.sort(new LexicographicalPointComparator());
    normalizedParetoFront.sort(new LexicographicalPointComparator());

    // STEP 4. Compute df and dl (See specifications in Deb's description of the metric)
    double df = PointUtils.euclideanDistance(normalizedFront.getPoint(0), normalizedParetoFront.getPoint(0)) ;
    double dl = PointUtils.euclideanDistance(
        normalizedFront.getPoint(normalizedFront.getNumberOfPoints()-1),
        normalizedParetoFront.getPoint(normalizedParetoFront.getNumberOfPoints()-1)) ;

    double mean = 0.0;
    double diversitySum = df + dl;

    int numberOfPoints = normalizedFront.getNumberOfPoints() ;

    // STEP 5. Calculate the mean of distances between points i and (i - 1).
    // (the points are in lexicografical order)
    for (int i = 0; i < (numberOfPoints - 1); i++) {
      mean += PointUtils.euclideanDistance(normalizedFront.getPoint(i), normalizedFront.getPoint(i + 1));
    }

    mean = mean / (double) (numberOfPoints - 1);

    // STEP 6. If there are more than a single point, continue computing the
    // metric. In other case, return the worse value (1.0, see metric's description).
    if (numberOfPoints > 1) {
      for (int i = 0; i < (numberOfPoints - 1); i++) {
        diversitySum += Math.abs(PointUtils.euclideanDistance(normalizedFront.getPoint(i),
          normalizedFront.getPoint(i + 1)) - mean);
      }
      return diversitySum / (df + dl + (numberOfPoints - 1) * mean);
    } else {
      return 1.0;
    }
  }

  @Override public String getName() {
    return NAME;
  }
}
