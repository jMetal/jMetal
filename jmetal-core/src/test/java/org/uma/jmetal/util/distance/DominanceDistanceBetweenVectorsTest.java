package org.uma.jmetal.util.distance;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.util.checking.exception.InvalidConditionException;
import org.uma.jmetal.util.checking.exception.NullParameterException;
import org.uma.jmetal.util.distance.impl.DominanceDistanceBetweenVectors;

import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DominanceDistanceBetweenVectorsTest {
  private static final double EPSILON = 0.0000000000001 ;

  private DominanceDistanceBetweenVectors distance ;

  @Before
  public void setup() {
    distance = new DominanceDistanceBetweenVectors() ;
  }

  @Test(expected = NullParameterException.class)
  public void shouldFirstPointToCompareEqualsToNullRaiseAnException() {
    distance.compute(null, new double[]{1, 2}) ;
  }

  @Test (expected = NullParameterException.class)
  public void shouldSecondPointToCompareEqualsToNullRaiseAnException() {
    distance.compute(new double[]{1, 2}, null) ;
  }

  @Test (expected = InvalidConditionException.class)
  public void shouldPassingPointsWithDifferentDimensionsRaiseAnException() {
    distance.compute(new double[]{1}, new double[]{1, 2, 3}) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithZeroDimensionReturnZero() {
    distance.compute(new double[]{}, new double[]{}) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithOneDimensionReturnTheCorrectValue() {
    assertEquals(4.0, distance.compute(new double[]{-2}, new double[]{2}), EPSILON) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseA() {
    assertEquals(0.1, distance.compute(new double[]{0.1, 0.7}, new double[]{0.2, 0.5}), EPSILON) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseB() {
    assertEquals(0.3, distance.compute(new double[]{0.1, 0.7}, new double[]{0.4, 0.3}), EPSILON) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseC() {
    assertEquals(0.2, distance.compute(new double[]{0.1, 0.7}, new double[]{0.3, 0.7}), EPSILON) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseD() {
    assertEquals(Math.sqrt(0.02), distance.compute(new double[]{0.2, 0.3}, new double[]{0.3, 0.4}), EPSILON) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseE() {
    assertEquals(Math.sqrt(0.09 + 0.09), distance.compute(new double[]{0.2, 0.3}, new double[]{0.5, 0.6}), EPSILON) ;
  }

  @Test public void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseF() {
    assertEquals(Math.sqrt(0.01 + 0.04), distance.compute(new double[]{0.6, 0.2}, new double[]{0.7, 0.4}), EPSILON) ;
  }
}
