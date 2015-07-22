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
 * This class implements the generational distance indicator.
 * Reference: Van Veldhuizen, D.A., Lamont, G.B.: Multiobjective Evolutionary
 * Algorithm Research: A History and Analysis.
 * Technical Report TR-98-03, Dept. Elec. Comput. Eng., Air Force
 * Inst. Technol. (1998)
 */
public class GenerationalDistance<Evaluate extends List<? extends Solution<?>>>
    extends NormalizableQualityIndicator<Evaluate, Double> {

  private static final double POW = 2.0;

  private Front referenceParetoFront ;

  /**
   *
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public GenerationalDistance(String referenceParetoFrontFile) throws FileNotFoundException {
    super("GD", "Generational distance quality indicator") ;
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
  public GenerationalDistance(Front referenceParetoFront) {
    super("GD", "Generational distance quality indicator") ;
    if (referenceParetoFront == null) {
      throw new JMetalException("The pareto front is null");
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

    return generationalDistance(new ArrayFront(solutionList), referenceParetoFront);
  }

  /**
   * Returns the generational distance value for a given front
   *
   * @param front           The front
   * @param referenceFront The reference pareto front
   */
  public double generationalDistance(Front front, Front referenceFront) {
    double[] maximumValue;
    double[] minimumValue;
    Front normalizedFront;
    Front normalizedParetoFront;

    // STEP 3. Sum the distances between each point of the front and the
    // nearest point in the true Pareto front
    double sum = 0.0;
    for (int i = 0; i < front.getNumberOfPoints(); i++) {
      sum += Math.pow(FrontUtils.distanceToClosestPoint(front.getPoint(i),
          referenceFront), POW);
    }

    // STEP 4. Obtain the sqrt of the sum
    sum = Math.pow(sum, 1.0 / POW);

    // STEP 5. Divide the sum by the maximum number of points of the front
    double generationalDistance = sum / front.getNumberOfPoints();

    return generationalDistance;
  }

  @Override
  public String getName() {
    return super.getName();
  }
}
