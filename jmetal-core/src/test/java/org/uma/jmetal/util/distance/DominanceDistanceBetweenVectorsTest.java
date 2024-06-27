package org.uma.jmetal.util.distance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.util.distance.impl.DominanceDistanceBetweenVectors;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * @author Antonio J. Nebro
 */
class DominanceDistanceBetweenVectorsTest {

  private static final double EPSILON = 0.0000000000001;

  private DominanceDistanceBetweenVectors distance;

  @BeforeEach
  void setup() {
    distance = new DominanceDistanceBetweenVectors();
  }

  @Test
  void shouldFirstPointToCompareEqualsToNullRaiseAnException() {
    assertThrows(NullParameterException.class,  ()->distance.compute(null, new double[]{1, 2}));
  }

  @Test
  void shouldSecondPointToCompareEqualsToNullRaiseAnException() {
    assertThrows(NullParameterException.class,  ()->distance.compute(new double[]{1, 2}, null));
  }

  @Test
  void shouldPassingPointsWithDifferentDimensionsRaiseAnException() {
    assertThrows(InvalidConditionException.class, () -> distance.compute(new double[]{1, 2, 3}, new double[]{1, 2}));
  }

  @Test
  void shouldCalculatingDistanceOfPointsWithZeroDimensionReturnZero() {
    distance.compute(new double[]{}, new double[]{});
  }

  @Test
  void shouldCalculatingDistanceOfPointsWithOneDimensionReturnTheCorrectValue() {
    assertEquals(4.0, distance.compute(new double[]{-2}, new double[]{2}), EPSILON);
  }

  @Test
  void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseA() {
    assertEquals(0.1, distance.compute(new double[]{0.1, 0.7}, new double[]{0.2, 0.5}), EPSILON);
  }

  @Test
  void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseB() {
    assertEquals(0.3, distance.compute(new double[]{0.1, 0.7}, new double[]{0.4, 0.3}), EPSILON);
  }

  @Test
  void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseC() {
    assertEquals(0.2, distance.compute(new double[]{0.1, 0.7}, new double[]{0.3, 0.7}), EPSILON);
  }

  @Test
  void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseD() {
    assertEquals(Math.sqrt(0.02), distance.compute(new double[]{0.2, 0.3}, new double[]{0.3, 0.4}),
        EPSILON);
  }

  @Test
  void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseE() {
    assertEquals(Math.sqrt(0.09 + 0.09),
        distance.compute(new double[]{0.2, 0.3}, new double[]{0.5, 0.6}), EPSILON);
  }

  @Test
  void shouldCalculatingDistanceOfPointsWithTwoDimensionsReturnTheCorrectValueCaseF() {
    assertEquals(Math.sqrt(0.01 + 0.04),
        distance.compute(new double[]{0.6, 0.2}, new double[]{0.7, 0.4}), EPSILON);
  }
}
