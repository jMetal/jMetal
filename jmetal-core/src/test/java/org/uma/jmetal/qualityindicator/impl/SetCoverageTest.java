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

package org.uma.jmetal.qualityindicator.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.imp.FrontUtils;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import org.uma.jmetal.util.point.impl.PointSolution;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class SetCoverageTest {
  private static final double EPSILON = 0.0000000000001 ;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  private SetCoverage setCoverage ;

  @Before public void setup() {
    setCoverage = new SetCoverage() ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheFrontApproximationIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The front A is null"));

    Front front = new ArrayFront(0, 0) ;
    setCoverage.execute(null, front) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheParetoFrontIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The front B is null"));

    Front front = new ArrayFront(0, 0) ;

    setCoverage.execute(front, null) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheFrontApproximationListIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The list A is null"));

    List<DoubleSolution> list = new ArrayList<>();
    setCoverage.execute(null, list) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheParetoFrontListIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The list B is null"));

    List<DoubleSolution> list = new ArrayList<>();
    setCoverage.execute(list, null) ;
  }

  @Test
  public void shouldExecuteReturnZeroIfTheFrontsContainOnePointWhichIsTheSame() {
    int numberOfPoints = 1 ;
    int numberOfDimensions = 3 ;
    Front frontA = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front frontB = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setDimensionValue(0, 10.0);
    point1.setDimensionValue(1, 12.0);
    point1.setDimensionValue(2, -1.0);

    frontA.setPoint(0, point1);
    frontB.setPoint(0, point1);

    assertEquals(0.0, setCoverage.execute(frontA, frontB), EPSILON);
  }

  @Test
  public void shouldExecuteReturnZeroIfBothFrontsAreEmpty() {
    int numberOfPoints = 0 ;
    int numberOfDimensions = 3 ;
    Front frontA = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front frontB = new ArrayFront(numberOfPoints, numberOfDimensions);

    assertEquals(0.0, setCoverage.execute(frontA, frontB), EPSILON);
  }

  @Test
  public void shouldExecuteReturnOneIfTheSecondFrontIsEmpty() {
    int numberOfDimensions = 2 ;
    Front frontA = new ArrayFront(1, numberOfDimensions);
    Front frontB = new ArrayFront(0, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setDimensionValue(0, 10.0);
    point1.setDimensionValue(1, 12.0);

    frontA.setPoint(0, point1);

    assertEquals(1.0, setCoverage.execute(frontA, frontB), EPSILON);
  }

  /**
   * Given a frontA with point [2,3] and a frontB with point [1,2], the value of the
   * setCoverage(frontA, frontB) == 0 and setCoverage(frontB, frontA) == 1
   */
  @Test
  public void shouldExecuteReturnTheRightValueIfTheFrontsContainOnePointWhichIsNotTheSame() {
    int numberOfPoints = 1 ;
    int numberOfDimensions = 2 ;
    Front frontA = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front frontB = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setDimensionValue(0, 2.0);
    point1.setDimensionValue(1, 3.0);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setDimensionValue(0, 1.0);
    point2.setDimensionValue(1, 2.0);

    frontA.setPoint(0, point1);
    frontB.setPoint(0, point2);

    assertEquals(0.0, setCoverage.execute(frontA, frontB), EPSILON);
    assertEquals(1.0, setCoverage.execute(frontB, frontA), EPSILON);
  }

  /**
   * Given a frontA with points [0.0,6.0], [2.0,3.0],[4.0,2.0] and a frontB with points
   * [1.0,7.0], [2.0,3.0], [3.5, 1.0], the value of setCoverage(frontA, frontB) == 1/3 and
   * setCoverage(frontB, frontA) == 1/3
   */
  @Test
  public void shouldExecuteReturnTheCorrectValueCaseA() {
    int numberOfPoints = 3 ;
    int numberOfDimensions = 2 ;
    Front frontA = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front frontB = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setDimensionValue(0, 0.0);
    point1.setDimensionValue(1, 6.0);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setDimensionValue(0, 2.0);
    point2.setDimensionValue(1, 3.0);
    Point point3 = new ArrayPoint(numberOfDimensions) ;
    point3.setDimensionValue(0, 4.0);
    point3.setDimensionValue(1, 2.0);

    frontA.setPoint(0, point1);
    frontA.setPoint(1, point2);
    frontA.setPoint(2, point3);

    Point point4 = new ArrayPoint(numberOfDimensions) ;
    point4.setDimensionValue(0, 1.0);
    point4.setDimensionValue(1, 7.0);
    Point point5 = new ArrayPoint(numberOfDimensions) ;
    point5.setDimensionValue(0, 2.0);
    point5.setDimensionValue(1, 3.0);
    Point point6 = new ArrayPoint(numberOfDimensions) ;
    point6.setDimensionValue(0, 3.5);
    point6.setDimensionValue(1, 1.0);

    frontB.setPoint(0, point4);
    frontB.setPoint(1, point5);
    frontB.setPoint(2, point6);

    assertEquals(1.0/3.0, setCoverage.execute(frontA, frontB), EPSILON);
    assertEquals(1.0/3.0, setCoverage.execute(frontB, frontA), EPSILON);
  }

  /**
   * Given a frontA with points [0.0,6.0], [2.0,3.0],[4.0,2.0] and a frontB with points
   * [1.0,7.0], [2.5,3.0], [5.0, 2.5], the value of setCoverage(frontA, frontB) == 1 and
   * setCoverage(frontB, frontA) == 0
   */
  @Test
  public void shouldExecuteReturnTheCorrectValueCaseB() {
    int numberOfPoints = 3 ;
    int numberOfDimensions = 2 ;
    Front frontA = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front frontB = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setDimensionValue(0, 0.0);
    point1.setDimensionValue(1, 6.0);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setDimensionValue(0, 2.0);
    point2.setDimensionValue(1, 3.0);
    Point point3 = new ArrayPoint(numberOfDimensions) ;
    point3.setDimensionValue(0, 4.0);
    point3.setDimensionValue(1, 2.0);

    frontA.setPoint(0, point1);
    frontA.setPoint(1, point2);
    frontA.setPoint(2, point3);

    Point point4 = new ArrayPoint(numberOfDimensions) ;
    point4.setDimensionValue(0, 1.0);
    point4.setDimensionValue(1, 7.0);
    Point point5 = new ArrayPoint(numberOfDimensions) ;
    point5.setDimensionValue(0, 2.5);
    point5.setDimensionValue(1, 3.0);
    Point point6 = new ArrayPoint(numberOfDimensions) ;
    point6.setDimensionValue(0, 5.0);
    point6.setDimensionValue(1, 2.5);

    frontB.setPoint(0, point4);
    frontB.setPoint(1, point5);
    frontB.setPoint(2, point6);

    assertEquals(1.0, setCoverage.execute(frontA, frontB), EPSILON);
    assertEquals(0.0, setCoverage.execute(frontB, frontA), EPSILON);
  }

  /**
   * The same case as shouldExecuteReturnTheCorrectValueCaseB() but using solution lists
   */
  @Test
  public void shouldExecuteReturnTheCorrectValueCaseC() {
    int numberOfPoints = 3 ;
    int numberOfDimensions = 2 ;
    Front frontA = new ArrayFront(numberOfPoints, numberOfDimensions);
    Front frontB = new ArrayFront(numberOfPoints, numberOfDimensions);

    Point point1 = new ArrayPoint(numberOfDimensions) ;
    point1.setDimensionValue(0, 0.0);
    point1.setDimensionValue(1, 6.0);
    Point point2 = new ArrayPoint(numberOfDimensions) ;
    point2.setDimensionValue(0, 2.0);
    point2.setDimensionValue(1, 3.0);
    Point point3 = new ArrayPoint(numberOfDimensions) ;
    point3.setDimensionValue(0, 4.0);
    point3.setDimensionValue(1, 2.0);

    frontA.setPoint(0, point1);
    frontA.setPoint(1, point2);
    frontA.setPoint(2, point3);

    Point point4 = new ArrayPoint(numberOfDimensions) ;
    point4.setDimensionValue(0, 1.0);
    point4.setDimensionValue(1, 7.0);
    Point point5 = new ArrayPoint(numberOfDimensions) ;
    point5.setDimensionValue(0, 2.5);
    point5.setDimensionValue(1, 3.0);
    Point point6 = new ArrayPoint(numberOfDimensions) ;
    point6.setDimensionValue(0, 5.0);
    point6.setDimensionValue(1, 2.5);

    frontB.setPoint(0, point4);
    frontB.setPoint(1, point5);
    frontB.setPoint(2, point6);

    List<PointSolution> listA = FrontUtils.convertFrontToSolutionList(frontA) ;
    List<PointSolution> listB = FrontUtils.convertFrontToSolutionList(frontB) ;

    assertEquals(1.0, setCoverage.execute(listA, listB), EPSILON);
    assertEquals(0.0, setCoverage.execute(listB, listA), EPSILON);
  }

  @Test
  public void shouldGetNameReturnTheCorrectValue() {
    assertEquals("SC", setCoverage.getName());
  }
}
