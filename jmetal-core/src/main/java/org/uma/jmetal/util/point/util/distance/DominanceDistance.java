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

package org.uma.jmetal.util.point.util.distance;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.Point;

/**
 * Computes the distance between two points a y b according to the dominance relationship. Point a
 * is supposed to be point of the Pareto front
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DominanceDistance implements PointDistance {

  @Override
  public double compute(Point a, Point b) {
    if (a == null) {
      throw new JMetalException("The first point is null") ;
    } else if (b == null) {
      throw new JMetalException("The second point is null") ;
    } else if (a.getNumberOfDimensions() != b.getNumberOfDimensions()) {
      throw new JMetalException("The dimensions of the points are different: "
          + a.getNumberOfDimensions() + ", " + b.getNumberOfDimensions()) ;
    }

    double distance = 0.0;

    for (int i = 0; i < a.getNumberOfDimensions(); i++) {
      double max = Math.max(b.getDimensionValue(i) - a.getDimensionValue(i), 0.0) ;
      distance += Math.pow(max, 2);
    }
    return Math.sqrt(distance);
  }
}
