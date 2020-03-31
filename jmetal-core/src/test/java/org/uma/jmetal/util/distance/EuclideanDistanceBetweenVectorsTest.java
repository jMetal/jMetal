package org.uma.jmetal.util.distance;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.util.checking.exception.InvalidConditionException;
import org.uma.jmetal.util.checking.exception.NullParameterException;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenVectors;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;

import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class EuclideanDistanceBetweenVectorsTest {
  private static final double EPSILON = 0.0000000000001 ;

  private EuclideanDistanceBetweenVectors distance ;

  @Before
  public void setup() {
    distance = new EuclideanDistanceBetweenVectors() ;
  }

  @Test(expected = NullParameterException.class)
  public void shouldFirstPointToCompareEqualsToNullRaiseAnException() {
    distance.compute(null, new double[]{1, 2}) ;
  }

  @Test (expected = NullParameterException.class)
  public void shouldSecondPointToCompareEqualsToNullRaiseAnException() {
    distance.compute(new double[]{1, 2}, null ) ;
  }

  @Test (expected = InvalidConditionException.class)
  public void shouldPassingPointsWithDifferentDimensionsRaiseAnException() {
    distance.compute(new double[]{1, 2}, new double[]{1, 2, 3}) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithZeroDimensionReturnZero() {
    assertEquals(0, distance.compute(new double[]{}, new double[]{}), EPSILON) ; ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithOneDimensionReturnTheCorrectValue() {
    Point point1 = new ArrayPoint(1) ;
    Point point2 = new ArrayPoint(1) ;

    point1.setValue(0, -2.0);
    point2.setValue(0, +2.0);

    assertEquals(4.0, distance.compute(new double[]{-2.0}, new double[]{2}), EPSILON) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseA() {
    assertEquals(Math.sqrt(0.02), distance.compute(new double[]{0.3, 0.4}, new double[]{0.2, 0.3}), EPSILON) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseB() {
    assertEquals(Math.sqrt(8.0), distance.compute(new double[]{0.0, 0.0}, new double[]{2, 2}), EPSILON) ;
  }
}
