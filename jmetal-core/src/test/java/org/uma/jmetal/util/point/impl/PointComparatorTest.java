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

import org.junit.Test;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.util.comparator.PointComparator;

import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class PointComparatorTest {
  private Point point1 ;
  private Point point2 ;

  private PointComparator comparator ;

  @Test(expected = JMetalException.class)
  public void shouldFirstPointToCompareEqualsToNullRaiseAnException() throws Exception {
    comparator = new PointComparator() ;

    point2 = new ArrayPoint(4) ;
    comparator.compare(null, point2);
  }

  @Test (expected = JMetalException.class)
  public void shouldSecondPointToCompareEqualsToNullRaiseAnException() throws Exception {
    comparator = new PointComparator() ;

    point1 = new ArrayPoint(4) ;
    comparator.compare(point1, null);
  }

  @Test (expected = JMetalException.class)
  public void shouldComparingDifferentLengthPointsRaiseAnException() throws Exception {
    point1 = new ArrayPoint(2) ;
    point2 = new ArrayPoint(3) ;

    comparator = new PointComparator() ;
    comparator.compare(point1, point2);
  }

  @Test
  public void shouldCompareReturnMinusOneIfTheFirstPointIsBetterThanTheSecondOneWhenMaximizing() {
    point1 = new ArrayPoint(2) ;
    point1.setDimensionValue(0, 1.0);
    point1.setDimensionValue(1, 3.0);

    point2 = new ArrayPoint(2) ;
    point2.setDimensionValue(0, 1.0);
    point2.setDimensionValue(1, 2.0);

    comparator = new PointComparator() ;
    comparator.setMaximizing();

    assertEquals(-1, comparator.compare(point1, point2)) ;
  }

  @Test
  public void shouldCompareReturnOneIfTheSecondPointIsBetterThanTheFirstOneWhenMaximizing() {
    point1 = new ArrayPoint(2) ;
    point1.setDimensionValue(0, 1.0);
    point1.setDimensionValue(1, 3.0);

    point2 = new ArrayPoint(2) ;
    point2.setDimensionValue(0, 1.0);
    point2.setDimensionValue(1, 5.0);

    comparator = new PointComparator() ;
    comparator.setMaximizing();

    assertEquals(1, comparator.compare(point1, point2)) ;
  }

  @Test
  public void shouldCompareBetterReturnZeroIfBothPointsAreEqualWhenMaximizing() {
    point1 = new ArrayPoint(2) ;
    point1.setDimensionValue(0, 1.0);
    point1.setDimensionValue(1, 3.0);

    point2 = new ArrayPoint(2) ;
    point2.setDimensionValue(0, 1.0);
    point2.setDimensionValue(1, 3.0);

    comparator = new PointComparator() ;
    comparator.setMaximizing();

    assertEquals(0, comparator.compare(point1, point2)) ;
  }

  @Test
  public void shouldCompareBetterReturnZeroIfBothPointsAreEqualWhenMinimizing() {
    point1 = new ArrayPoint(2) ;
    point1.setDimensionValue(0, 1.0);
    point1.setDimensionValue(1, 3.0);

    point2 = new ArrayPoint(2) ;
    point2.setDimensionValue(0, 1.0);
    point2.setDimensionValue(1, 3.0);

    comparator = new PointComparator() ;
    comparator.setMinimizing();

    assertEquals(0, comparator.compare(point1, point2)) ;
  }
}
