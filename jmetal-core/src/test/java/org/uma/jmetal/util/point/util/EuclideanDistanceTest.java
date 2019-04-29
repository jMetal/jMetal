package org.uma.jmetal.util.point.util;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;
import org.uma.jmetal.util.point.util.distance.EuclideanDistance;

import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class EuclideanDistanceTest {
  private static final double EPSILON = 0.0000000000001 ;

  private EuclideanDistance distance ;

  @Before
  public void setup() {
    distance = new EuclideanDistance() ;
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

    point1.setValue(0, -2.0);
    point2.setValue(0, +2.0);

    assertEquals(4.0, distance.compute(point1, point2), EPSILON) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseA() {
    Point point1 = new ArrayPoint(2) ;
    Point point2 = new ArrayPoint(2) ;

    point1.setValue(0, 0.3);
    point1.setValue(1, 0.4);
    point2.setValue(0, 0.2);
    point2.setValue(1, 0.3);

    assertEquals(Math.sqrt(0.02), distance.compute(point1, point2), EPSILON) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseB() {
    Point point1 = new ArrayPoint(2) ;
    Point point2 = new ArrayPoint(2) ;

    point1.setValue(0, 0.0);
    point1.setValue(1, 0.0);
    point2.setValue(0, +2.0);
    point2.setValue(1, +2.0);

    assertEquals(Math.sqrt(8.0), distance.compute(point1, point2), EPSILON) ;
  }
}
