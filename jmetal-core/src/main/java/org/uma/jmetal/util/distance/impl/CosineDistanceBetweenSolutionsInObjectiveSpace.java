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

package org.uma.jmetal.util.distance.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.distance.Distance;

/**
 * Class for calculating the cosene distance between two {@link Solution} objects in objective space.
 *
 * @author <antonio@lcc.uma.es>
 */
public class CosineDistanceBetweenSolutionsInObjectiveSpace<S extends Solution<?>>
    implements Distance<S, S> {

  private S referencePoint;

  public CosineDistanceBetweenSolutionsInObjectiveSpace(S referencePoint) {
    this.referencePoint = referencePoint ;
  }

  @Override
  public double getDistance(S solution1, S solution2) {
    double sum = 0.0 ;
    for (int i = 0; i < solution1.getNumberOfObjectives(); i++) {
      sum += (solution1.getObjective(i) - referencePoint.getObjective(i)) *
          (solution2.getObjective(i) - referencePoint.getObjective(i));
    }

    double result = sum / (sumOfDistancesToIdealPoint(solution1) * sumOfDistancesToIdealPoint(solution2)) ;

    return result ;
  }

  private double sumOfDistancesToIdealPoint(S solution) {
    double sum = 0.0 ;

    for (int i = 0 ; i < solution.getNumberOfObjectives(); i++) {
      sum += Math.pow(solution.getObjective(i) - referencePoint.getObjective(i), 2.0) ;
    }

    return Math.sqrt(sum) ;
  }
}
