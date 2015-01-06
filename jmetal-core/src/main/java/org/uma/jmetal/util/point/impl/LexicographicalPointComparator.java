//  LexicoGraphicalComparator.java
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

package org.uma.jmetal.util.point.impl;

import org.uma.jmetal.util.point.Point;

import java.util.Comparator;


/**
 * This class implements the Comparator interface for comparing tow points.
 * The order used is lexicograhphical order.
 */
public class LexicographicalPointComparator implements Comparator<Point> {

  /**
   * The compare method compare the objects o1 and o2.
   *
   * @param pointOne An object that reference a double[]
   * @param pointTwo An object that reference a double[]
   * @return The following value: -1 if o1 < o1, 1 if o1 > o2 or 0 in other case.
   */
  @Override
  public int compare(Point pointOne, Point pointTwo) {
    // Determine the first i such as pointOne[i] != pointTwo[i];
    int index = 0;
    while ((index < pointOne.getNumberOfDimensions())
        && (index < pointTwo.getNumberOfDimensions())
        && pointOne.getDimensionValue(index) == pointTwo.getDimensionValue(index)) {
      index++;
    }

    if ((index >= pointOne.getNumberOfDimensions()) || (index >= pointTwo.getNumberOfDimensions())) {
      return 0;
    } else if (pointOne.getDimensionValue(index) < pointTwo.getDimensionValue(index)) {
      return -1;
    } else if (pointOne.getDimensionValue(index) > pointTwo.getDimensionValue(index)) {
      return 1;
    }
    return 0;
  }
}
