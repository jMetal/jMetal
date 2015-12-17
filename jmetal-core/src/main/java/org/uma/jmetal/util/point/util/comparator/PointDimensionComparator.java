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

package org.uma.jmetal.util.point.util.comparator;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.Point;

import java.util.Comparator;

/**
 * This class implements the {@link Comparator} interface. It is used
 * to compare two points according the value of a particular dimension.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
public class PointDimensionComparator implements Comparator<Point> {

  /**
   * Stores the value of the index to compare
   */
  private int index;

  /**
   * Constructor
   */
  public PointDimensionComparator(int index) {
    if (index < 0) {
      throw new JMetalException("The index value is negative");
    }
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
    if (pointOne ==  null) {
      throw new JMetalException("PointOne is null") ;
    } else if (pointTwo == null) {
      throw new JMetalException("PointTwo is null") ;
    } else if (index >= pointOne.getNumberOfDimensions()) {
      throw new JMetalException("The index value " + index
          + " is out of range (0,  " + (pointOne.getNumberOfDimensions()-1) + ")") ;
    } else if (index >= pointTwo.getNumberOfDimensions()) {
      throw new JMetalException("The index value " + index
          + " is out of range (0,  " + (pointTwo.getNumberOfDimensions()-1) + ")") ;
    }

    if (pointOne.getDimensionValue(index) < pointTwo.getDimensionValue(index)) {
      return -1;
    } else if (pointOne.getDimensionValue(index) > pointTwo.getDimensionValue(index)) {
      return 1;
    } else {
      return 0;
    }
  }
}
