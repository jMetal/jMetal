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

package org.uma.jmetal.util.front.imp;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class FrontUtilsTest {
  private static final double EPSILON = 0.0000000000001 ;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldGetMaximumValuesRaiseAnExceptionIfTheFrontIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The front is null"));

    FrontUtils.getMaximumValues(null) ;
  }

  @Test
  public void shouldGetMaximumValuesRaiseAnExceptionIfTheFrontIsEmpty() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The front is empty"));

    Front front = new ArrayFront(0, 0);

    FrontUtils.getMaximumValues(front) ;
  }

  @Test
  public void shouldGetMaximumValuesWithAFrontWithOnePointReturnTheCorrectValue() {
    int numberOfPoints = 1 ;
    int numberOfDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point = new ArrayPoint(numberOfDimensions) ;
    point.setDimensionValue(0, 10.0);
    point.setDimensionValue(1, 12.0);

    front.setPoint(0, point);

    double[] expectedResult = {10.0, 12.0} ;

    assertArrayEquals(expectedResult, FrontUtils.getMaximumValues(front), EPSILON) ;
  }

  @Test
  public void shouldGetMaximumValuesWithAFrontWithThreePointReturnTheCorrectValue() {
    int numberOfPoints = 3 ;
    int numberOfDimensions = 3 ;
    Front front = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setDimensionValue(0, 10.0);
    point1.setDimensionValue(1, 12.0);
    point1.setDimensionValue(2, -1.0);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setDimensionValue(0, 8.0);
    point2.setDimensionValue(1, 80.0);
    point2.setDimensionValue(2, 0.32);
    Point point3 = new ArrayPoint(numberOfDimensions) ;
    point3.setDimensionValue(0, 5.0);
    point3.setDimensionValue(1, 50.0);
    point3.setDimensionValue(2, 3.0);

    front.setPoint(0, point1);
    front.setPoint(1, point2);
    front.setPoint(2, point3);

    double[] expectedResult = {10.0, 80.0, 3.0} ;

    assertArrayEquals(expectedResult, FrontUtils.getMaximumValues(front), EPSILON) ;
  }

  @Test
  public void shouldGetMinimumValuesRaiseAnExceptionIfTheFrontIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The front is null"));

    FrontUtils.getMinimumValues(null) ;
  }

  @Test
  public void shouldGetMinimumValuesRaiseAnExceptionIfTheFrontIsEmpty() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The front is empty"));

    Front front = new ArrayFront(0, 0);

    FrontUtils.getMinimumValues(front) ;
  }

  @Test
  public void shouldGetMinimumValuesWithAFrontWithOnePointReturnTheCorrectValue() {
    int numberOfPoints = 1 ;
    int numberOfDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point = new ArrayPoint(numberOfDimensions) ;
    point.setDimensionValue(0, 10.0);
    point.setDimensionValue(1, 12.0);

    front.setPoint(0, point);

    double[] expectedResult = {10.0, 12.0} ;

    assertArrayEquals(expectedResult, FrontUtils.getMaximumValues(front), EPSILON) ;
  }

  @Test
  public void shouldGetMinimumValuesWithAFrontWithThreePointReturnTheCorrectValue() {
    int numberOfPoints = 3 ;
    int numberOfDimensions = 3 ;
    Front front = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setDimensionValue(0, 10.0);
    point1.setDimensionValue(1, 12.0);
    point1.setDimensionValue(2, -1.0);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setDimensionValue(0, 8.0);
    point2.setDimensionValue(1, 80.0);
    point2.setDimensionValue(2, 0.32);
    Point point3 = new ArrayPoint(numberOfDimensions) ;
    point3.setDimensionValue(0, 5.0);
    point3.setDimensionValue(1, 50.0);
    point3.setDimensionValue(2, 3.0);

    front.setPoint(0, point1);
    front.setPoint(1, point2);
    front.setPoint(2, point3);

    double[] expectedResult = {5.0, 12.0, -1.0} ;

    assertArrayEquals(expectedResult, FrontUtils.getMinimumValues(front), EPSILON) ;
  }

  @Test
  public void shouldGetNormalizedFrontRaiseAnExceptionIfTheFrontIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The front is null"));

    FrontUtils.getNormalizedFront(null, new double[0], new double[0]) ;
  }

  @Test
  public void shouldGetNormalizedFrontRaiseAnExceptionIfTheFrontIsEmpty() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The front is empty"));

    Front front = new ArrayFront(0, 0);

    FrontUtils.getNormalizedFront(front, new double[0], new double[0]) ;
  }

  @Test
  public void shouldGetNormalizedFrontRaiseAnExceptionIfMaximumArrayIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The maximum values array is null"));

    Front front = new ArrayFront(1, 2);

    FrontUtils.getNormalizedFront(front, null, new double[0]) ;
  }

  @Test
  public void shouldGetNormalizedFrontRaiseAnExceptionIfMinimumArrayIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The minimum values array is null"));

    Front front = new ArrayFront(1, 2);

    FrontUtils.getNormalizedFront(front, new double[0], null) ;
  }

  @Test
  public void shouldGetNormalizedFrontRaiseAnExceptionTheDimensionOfTheMaximumAndMinimumArrayIsNotEqual() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The length of the maximum array (4) "
        + "is different from the length of the minimum array (2)"));

    int numberOfPointDimensions = 2 ;

    Front front = new ArrayFront(1, numberOfPointDimensions);

    FrontUtils.getNormalizedFront(front, new double[4], new double[2]) ;
  }

  @Test
  public void shouldGetNormalizedFrontRaiseAnExceptionTheDimensionOfTheMaximumArrayPointsIsNotCorrect() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The length of the point dimensions (2) " +
       "is different from the length of the maximum array (4)"));

    int numberOfPointDimensions = 2 ;

    Front front = new ArrayFront(1, numberOfPointDimensions);

    FrontUtils.getNormalizedFront(front, new double[4], new double[4]) ;
  }

  /**
   * Point: [2,4]
   * Maximum values: [4, 4]
   * Minimum values: [0, 0]
   * Result: [0.5, 1.0]
   */
  @Test
  public void shouldGetNormalizedFrontReturnTheCorrectFrontIfThisContainsOnePoint() {
    int numberOfPoints = 1 ;
    int numberOfDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point = new ArrayPoint(numberOfDimensions) ;
    point.setDimensionValue(0, 2);
    point.setDimensionValue(1, 4);

    front.setPoint(0, point);

    double[] minimum = {0, 0} ;
    double[] maximum = {4, 4} ;

    Front normalizedFront = FrontUtils.getNormalizedFront(front, maximum, minimum) ;
    assertEquals(0.5, normalizedFront.getPoint(0).getDimensionValue(0), EPSILON) ;
    assertEquals(1.0, normalizedFront.getPoint(0).getDimensionValue(1), EPSILON) ;
  }

  /**
   * Points: [2,4], [-2, 3]
   * Maximum values: [6, 8]
   * Minimum values: [-10, 1]
   * Result: [0.5, 1.0], []
   */
  @Test
  public void shouldGetNormalizedFrontReturnTheCorrectFrontIfThisContainsTwoPoint() {
    int numberOfPoints = 2 ;
    int numberOfDimensions = 2 ;
    Front front = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setDimensionValue(0, 2);
    point1.setDimensionValue(1, 4);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setDimensionValue(0, -2);
    point2.setDimensionValue(1, 3);

    front.setPoint(0, point1);
    front.setPoint(1, point2);

    double[] minimum = {-10, 1} ;
    double[] maximum = {6, 8} ;

    Front normalizedFront = FrontUtils.getNormalizedFront(front, maximum, minimum) ;
    assertEquals(0.75, normalizedFront.getPoint(0).getDimensionValue(0), EPSILON) ;
    assertEquals(3.0/7.0, normalizedFront.getPoint(0).getDimensionValue(1), EPSILON) ;
    assertEquals(0.5, normalizedFront.getPoint(1).getDimensionValue(0), EPSILON) ;
    assertEquals(2.0/7.0, normalizedFront.getPoint(1).getDimensionValue(1), EPSILON) ;
  }

}
