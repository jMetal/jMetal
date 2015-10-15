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
//

//  CREDIT
//  This class is based on the code of the wfg group (http://www.wfg.csse.uwa.edu.au/hypervolume/)
//  Copyright (C) 2010 Lyndon While, Lucas Bradstreet.

package org.uma.jmetal.util.point.util.comparator;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.Point;

import java.util.Comparator;

/**
 * Point comparator. Starts the comparison from front last point dimension to the first one
 *
 * @author Antonio J. Nebro
 */
public class PointComparator implements Comparator<Point> {
  private boolean maximizing;

  public PointComparator() {
    this.maximizing = true;
  }

  public void setMaximizing() {
    maximizing = true ;
  }

  public void setMinimizing() {
    maximizing = false ;
  }
  /**
   * Compares two Point objects
   *
   * @param pointOne An object that reference a Point
   * @param pointTwo An object that reference a Point
   * @return -1 if o1 < o1, 1 if o1 > o2 or 0 in other case.
   */
  @Override
  public int compare(Point pointOne, Point pointTwo) {
    if (pointOne ==  null) {
      throw new JMetalException("PointOne is null") ;
    } else if (pointTwo == null) {
      throw new JMetalException("PointTwo is null") ;
    } else if (pointOne.getNumberOfDimensions() != pointTwo.getNumberOfDimensions()) {
      throw new JMetalException("Points have different size: "
          + pointOne.getNumberOfDimensions()+ " and "
          + pointTwo.getNumberOfDimensions()) ;
    }

    for (int i = pointOne.getNumberOfDimensions()-1; i >= 0; i--) {
      if (isBetter(pointOne.getDimensionValue(i), pointTwo.getDimensionValue(i))) {
        return -1;
      } else if (isBetter(pointTwo.getDimensionValue(i), pointOne.getDimensionValue(i))) {
        return 1;
      }
    }
    return 0;
  }

  private boolean isBetter(double v1, double v2) {
    if (maximizing) {
      return (v1 > v2);
    } else {
      return (v2 > v1);
    }
  }
}
