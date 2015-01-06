//  ValueComparator.java
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
 * This class implements the <code>Comparator</code> interface. It is used
 * to compare two points according the value of a particular dimension.
 */
public class PointDimensionComparator implements Comparator<Point> {

  /**
   * Stores the value of the index to compare
   */
  private int index;

  /**
   * Constructor.
   * Creates a new instance of ValueComparator
   */
  public PointDimensionComparator(int index) {
    this.index = index;
  }

  /**
   * Compares the objects o1 and o2.
   *
   * @param pointOne An object that reference a double[]
   * @param pointTwo An object that reference a double[]
   * @return -1 if o1 < o1, 1 if o1 > o2 or 0 in other case.
   */
  @Override
  public int compare(Point pointOne, Point pointTwo) {
    if (pointOne.getDimensionValue(index) < pointTwo.getDimensionValue(index)) {
      return -1;
    } else if (pointOne.getDimensionValue(index) > pointTwo.getDimensionValue(index)) {
      return 1;
    } else {
      return 0;
    }
  }
}
