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
import org.uma.jmetal.util.point.Point;

import java.util.List;

/**
 * The Error Ratio (ER) quality indicator reports the ratio of solutions in a front of points
 * that are not members of the true Pareto front.
 *
 * NOTE: the indicator merely checks if the solutions in the front are not members of the
 * second front. No assumption is made about the second front is a true Pareto front, i.e,
 * the front could contain solutions that dominate some of those of the supposed Pareto front.
 * It is a responsibility of the caller to ensure that this does not happen.
 */
public class ErrorRatio implements QualityIndicator {
  private static final String NAME = "ER" ;

  @Override
  public double execute(Front paretoFrontApproximation, Front trueParetoFront) {
    if (paretoFrontApproximation == null) {
      throw new JMetalException("The pareto front approximation object is null") ;
    } else if (trueParetoFront == null) {
      throw new JMetalException("The pareto front object is null");
    }

    return er(paretoFrontApproximation, trueParetoFront) ;
  }

  @Override
  public double execute(List<? extends Solution> paretoFrontApproximation,
      List<? extends Solution> trueParetoFront) {

    if (paretoFrontApproximation == null) {
      throw new JMetalException("The pareto front approximation list is null") ;
    } else if (trueParetoFront == null) {
      throw new JMetalException("The pareto front list is null");
    }

    return this.execute(new ArrayFront(paretoFrontApproximation), new ArrayFront(trueParetoFront)) ;
  }

  /**
   * Returns the value of the error ratio indicator.
   *
   * @param front Solution front
   * @param referenceFront True Pareto front
   *
   * @return the value of the error ratio indicator
   * @throws org.uma.jmetal.util.JMetalException
   */
  private double er(Front front, Front referenceFront) throws JMetalException {
    int numberOfObjectives = referenceFront.getPointDimensions() ;
    double sum = 0;

    for (int i = 0; i < front.getNumberOfPoints(); i++) {
      Point currentPoint = front.getPoint(i);
      boolean thePointIsInTheParetoFront = false;
      for (int j = 0; j < referenceFront.getNumberOfPoints(); j++) {
        Point currentParetoFrontPoint = referenceFront.getPoint(j);
        boolean found = true;
        for (int k = 0; k < numberOfObjectives; k++) {
          if(currentPoint.getDimensionValue(k) != currentParetoFrontPoint.getDimensionValue(k)){
            found = false;
            break;
          }
        }
        if(found){
          thePointIsInTheParetoFront = found;
          break;
        }
      }
      if(!thePointIsInTheParetoFront){
        sum++;
      }
    }

    return sum / front.getNumberOfPoints();
  }


  @Override public String getName() {
    return NAME ;
  }
}
