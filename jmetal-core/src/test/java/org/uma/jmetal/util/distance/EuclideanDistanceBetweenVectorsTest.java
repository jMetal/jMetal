package org.uma.jmetal.util.distance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenVectors;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

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

  @Test
  public void shouldFirstPointToCompareEqualsToNullRaiseAnException() {
    assertThrows(NullParameterException.class, () -> distance.compute(null, new double[]{1, 2})) ;
  }

  @Test
  public void shouldSecondPointToCompareEqualsToNullRaiseAnException() {
    assertThrows(NullParameterException.class, () -> distance.compute(new double[]{1, 2}, null)) ;
  }

  @Test
  public void shouldPassingPointsWithDifferentDimensionsRaiseAnException() {
    assertThrows(InvalidConditionException.class, () -> distance.compute(new double[]{1, 2}, new double[]{1, 2, 3})) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithZeroDimensionReturnZero() {
    assertEquals(0, distance.compute(new double[]{}, new double[]{}), EPSILON) ; ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithOneDimensionReturnTheCorrectValue() {
    assertEquals(4.0, distance.compute(new double[]{-2.0}, new double[]{2}), EPSILON) ;
  }

  /*
   * Case A: the distance between points {0.3, 0.4} and {0.2, 0.3} must be 0.02
   */
  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseA() {
    assertEquals(Math.sqrt(0.02), distance.compute(new double[]{0.3, 0.4}, new double[]{0.2, 0.3}), EPSILON) ;
  }

  /*
   * Case B: the distance between points {0.0, 0.0} and {2.0, 2.0} must be 8.0
   */
  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseB() {
    assertEquals(Math.sqrt(8.0), distance.compute(new double[]{0.0, 0.0}, new double[]{2, 2}), EPSILON) ;
  }
}
