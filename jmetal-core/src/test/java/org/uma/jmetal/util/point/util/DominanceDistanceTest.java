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

package org.uma.jmetal.util.point.util;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import org.uma.jmetal.util.point.util.distance.DominanceDistance;

import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DominanceDistanceTest {
  private static final double EPSILON = 0.000000000001 ;

  private DominanceDistance distance ;

  @Before
  public void setup() {
    distance = new DominanceDistance() ;
  }

  @Test(expected = JMetalException.class)
  public void shouldFirstPointToCompareEqualsToNullRaiseAnException() {
    Point point = new ArrayPoint(5) ;

    distance.compute(null, point) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldSecondPointToCompareEqualsToNullRaiseAnException() {
    Point point = new ArrayPoint(5) ;

    distance.compute(point, null) ;
  }

  @Test (expected = JMetalException.class)
  public void shouldPassingPointsWithDifferentDimensionsRaiseAnException() {
    Point point1 = new ArrayPoint(5) ;
    Point point2 = new ArrayPoint(2) ;

    distance.compute(point1, point2) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithZeroDimensionReturnZero() {
    distance.compute(new ArrayPoint(0), new ArrayPoint(0)) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithOneDimensionReturnTheCorrectValue() {
    Point point1 = new ArrayPoint(1) ;
    Point point2 = new ArrayPoint(1) ;

    point1.setDimensionValue(0, -2.0);
    point2.setDimensionValue(0, +2.0);

    assertEquals(4.0, distance.compute(point1, point2), EPSILON) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseA() {
    Point point1 = new ArrayPoint(2) ;
    Point point2 = new ArrayPoint(2) ;

    point1.setDimensionValue(0, 0.1);
    point1.setDimensionValue(1, 0.7);

    point2.setDimensionValue(0, 0.2);
    point2.setDimensionValue(1, 0.5);

    assertEquals(0.1, distance.compute(point1, point2), EPSILON) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseB() {
    Point point1 = new ArrayPoint(2) ;
    Point point2 = new ArrayPoint(2) ;

    point1.setDimensionValue(0, 0.1);
    point1.setDimensionValue(1, 0.7);

    point2.setDimensionValue(0, 0.4);
    point2.setDimensionValue(1, 0.3);

    assertEquals(0.3, distance.compute(point1, point2), EPSILON) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseC() {
    Point point1 = new ArrayPoint(2) ;
    Point point2 = new ArrayPoint(2) ;

    point1.setDimensionValue(0, 0.1);
    point1.setDimensionValue(1, 0.7);

    point2.setDimensionValue(0, 0.3);
    point2.setDimensionValue(1, 0.7);

    assertEquals(0.2, distance.compute(point1, point2), EPSILON) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseD() {
    Point point1 = new ArrayPoint(2) ;
    Point point2 = new ArrayPoint(2) ;

    point1.setDimensionValue(0, 0.2);
    point1.setDimensionValue(1, 0.3);

    point2.setDimensionValue(0, 0.3);
    point2.setDimensionValue(1, 0.4);

    assertEquals(Math.sqrt(0.02), distance.compute(point1, point2), EPSILON) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseE() {
    Point point1 = new ArrayPoint(2) ;
    Point point2 = new ArrayPoint(2) ;

    point1.setDimensionValue(0, 0.2);
    point1.setDimensionValue(1, 0.3);

    point2.setDimensionValue(0, 0.5);
    point2.setDimensionValue(1, 0.6);

    assertEquals(Math.sqrt(0.09 + 0.09), distance.compute(point1, point2), EPSILON) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseF() {
    Point point1 = new ArrayPoint(2) ;
    Point point2 = new ArrayPoint(2) ;

    point1.setDimensionValue(0, 0.6);
    point1.setDimensionValue(1, 0.2);

    point2.setDimensionValue(0, 0.7);
    point2.setDimensionValue(1, 0.4);

    assertEquals(Math.sqrt(0.01 + 0.04), distance.compute(point1, point2), EPSILON) ;
  }
}
