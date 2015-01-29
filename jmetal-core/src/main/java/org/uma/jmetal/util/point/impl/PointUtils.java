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

package org.uma.jmetal.util.point.impl;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.Point;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class PointUtils {
  /**
   * This method returns the distance (taken the euclidean distance) between
   * two points
   *
   * @param a A point
   * @param b A point
   * @return The euclidean distance between the points
   */
  public static double euclideanDistance(Point a, Point b) {
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
      distance += Math.pow(a.getDimensionValue(i) - b.getDimensionValue(i), 2.0);
    }
    return Math.sqrt(distance);
  }
}
