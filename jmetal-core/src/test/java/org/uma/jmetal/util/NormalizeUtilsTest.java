package org.uma.jmetal.util;

import org.junit.Test;
import org.uma.jmetal.util.checking.exception.InvalidConditionException;

import java.lang.reflect.Constructor;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Test Class responsible for testing {@link NormalizeUtils}
 *
 * @author Thiago Ferreira
 * @version 1.0.0
 * @since 2018-12-16
 */
public class NormalizeUtilsTest {

  private static final double EPSILON = 0.0000000001;

  @Test(expected = ReflectiveOperationException.class)
  public void shouldThrowExceptionWhenInitiateThisClass() throws Exception {

    final Class<?> cls = NormalizeUtils.class;

    final Constructor<?> c = cls.getDeclaredConstructors()[0];

    c.setAccessible(true);

    c.newInstance((Object[]) null);
  }

  @Test(expected = InvalidConditionException.class)
  public void shouldThrowAnExceptionWhenMinAndMaxValuesAreTheSame() {
    NormalizeUtils.normalize(2, 10, 10);
  }

  @Test
  public void shouldReturnTheCorrectNumberWhenRangeIsZeroAndOne() {
    assertEquals(0.0, NormalizeUtils.normalize(10, 10, 20), EPSILON);
    assertEquals(0.5, NormalizeUtils.normalize(15, 10, 20), EPSILON);
    assertEquals(1.0, NormalizeUtils.normalize(20, 10, 20), EPSILON);
  }

  @Test
  public void shouldReturnTheCorrectNumberWhenRangeIsMinusOneAndPlusOne() {
    assertEquals(-1.0, NormalizeUtils.normalize(10, -1, 1, 10, 20), EPSILON);
    assertEquals(0.0, NormalizeUtils.normalize(15, -1, 1, 10, 20), EPSILON);
    assertEquals(1.0, NormalizeUtils.normalize(20, -1, 1, 10, 20), EPSILON);
  }

  @Test
  public void shouldGetNormalizedVectorGiveTheRightValue() {
    double[] vector = {1.0, 2.0, 3.0, 4.0};
    double[] normalizedVector = NormalizeUtils.getNormalizedVector(vector, 1.0, 4.0);

    assertArrayEquals(
        new double[] {(1.0 - 1.0) / 3.0, (2.0 - 1.0) / 3.0, (3.0 - 1.0) / 3.0, (4.0 - 1.0) / 3.0},
        normalizedVector,
        0.00001);
  }

  @Test
  public void shouldGetMinValuesOfTheColumnsOfAMatrixReturnTheRightValuesWhenTheMatrixHasOneRow() {
    double[][] matrix = new double[1][];
    matrix[0] = new double[] {1, 2, 3, 4};

    assertArrayEquals(
        new double[] {1, 2, 3, 4},
        NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(matrix),
        0.00001);
  }

  @Test
  public void shouldGetMinValuesOfTheColumnsOfAMatrixReturnTheRightValuesWhenTheMatrixHasTwoRows() {
    double[][] matrix = new double[2][];
    matrix[0] = new double[] {1, 2, 3, 4};
    matrix[1] = new double[] {0, 2, 4, 2};

    assertArrayEquals(
        new double[] {0, 2, 3, 2},
        NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(matrix),
        0.00001);
  }

  @Test
  public void
      shouldGetMinValuesOfTheColumnsOfAMatrixReturnTheRightValuesWhenTheMatrixHasThreeRows() {
    double[][] matrix = new double[3][];
    matrix[0] = new double[] {1, 2, 3, 4};
    matrix[1] = new double[] {0, 2, 4, 2};
    matrix[2] = new double[] {-1, 1, 6, 1};

    assertArrayEquals(
        new double[] {-1, 1, 3, 1},
        NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(matrix),
        0.00001);
  }

  @Test
  public void shouldGetMaxValuesOfTheColumnsOfAMatrixReturnTheRightValuesWhenTheMatrixHasOneRow() {
    double[][] matrix = new double[1][];
    matrix[0] = new double[] {1, 2, 3, 4};

    assertArrayEquals(
        new double[] {1, 2, 3, 4},
        NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(matrix),
        0.00001);
  }

  @Test
  public void shouldGetMaxValuesOfTheColumnsOfAMatrixReturnTheRightValuesWhenTheMatrixHasTwoRows() {
    double[][] matrix = new double[2][];
    matrix[0] = new double[] {1, 2, 3, 4};
    matrix[1] = new double[] {0, 2, 4, 2};

    assertArrayEquals(
        new double[] {1, 2, 4, 4},
        NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(matrix),
        0.00001);
  }

  @Test
  public void
      shouldGetMaxValuesOfTheColumnsOfAMatrixReturnTheRightValuesWhenTheMatrixHasThreeRows() {
    double[][] matrix = new double[3][];
    matrix[0] = new double[] {1, 2, 3, 4};
    matrix[1] = new double[] {0, 2, 4, 2};
    matrix[2] = new double[] {-1, 1, 6, 1};

    assertArrayEquals(
        new double[] {1, 2, 6, 4},
        NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(matrix),
        0.00001);
  }
}
