package org.uma.jmetal.util;

import java.util.Arrays;
import java.util.stream.IntStream;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * A utility class providing methods for normalizing numerical data to specified ranges.
 * This class supports various normalization operations including min-max scaling of individual
 * values, vectors, and matrices. It is particularly useful in optimization problems for
 * normalizing objective values and decision variables.
 *
 * <p>Key features:
 * <ul>
 *   <li>Min-max normalization to arbitrary ranges</li>
 *   <li>Support for single values, vectors, and matrices</li>
 *   <li>Handling of edge cases (e.g., when min equals max)</li>
 *   <li>Column-wise normalization for matrices</li>
 * </ul>
 *
 * <p>Invalid arguments are rejected with an
 * {@link org.uma.jmetal.util.errorchecking.exception.InvalidConditionException} (or a
 * {@link org.uma.jmetal.util.errorchecking.exception.NullParameterException} for a null argument):
 * a lower bound greater than the upper one; a matrix that is empty, has no columns, has rows of
 * different lengths, or contains a NaN or infinite value; and bounds that are not finite or whose
 * number is not that of the columns of the matrix. A value out of the range of the bounds is not
 * rejected: it is mapped outside the target range.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Normalize a single value from [0, 100] to [0, 1]
 * double normalized = NormalizeUtils.normalize(75.0, 0.0, 1.0, 0.0, 100.0);
 *
 * // Normalize a matrix (each column independently)
 * double[][] normalizedMatrix = NormalizeUtils.normalize(dataMatrix);
 * }</pre>
 *
 * @author Thiago Ferreira
 * @author Antonio J. Nebro
 * @since 1.0.0
 */
public class NormalizeUtils {
  private NormalizeUtils() throws InstantiationException {
    throw new InstantiationException("Instances of this type are forbidden");
  }

  /**
   * Normalizes a value from a source range to a target range using min-max scaling.
   * If the source range (max - min) is zero, returns the midpoint of the target range.
   *
   * <p>Formula: targetMin + ((value - sourceMin) * (targetMax - targetMin)) / (sourceMax - sourceMin)
   *
   * <p>Example:
   * <pre>{@code
   * // Normalize 75 from [0, 100] to [0, 1] -> 0.75
   * double n = normalize(75.0, 0.0, 1.0, 0.0, 100.0);
   * }</pre>
   *
   * @param value the input value to normalize
   * @param minRangeValue the minimum value of the target range (inclusive)
   * @param maxRangeValue the maximum value of the target range (inclusive)
   * @param min the minimum value of the source range (inclusive)
   * @param max the maximum value of the source range (inclusive)
   * @return the normalized value in the target range
   * @throws org.uma.jmetal.util.errorchecking.exception.InvalidConditionException if min > max or
   *     minRangeValue > maxRangeValue (or any of them is NaN)
   */
  public static double normalize(
          double value, double minRangeValue, double maxRangeValue, double min, double max) {
    Check.that(
        minRangeValue <= maxRangeValue,
        () -> "The lower bound of the target range (" + minRangeValue
            + ") is greater than the upper bound (" + maxRangeValue + ")");
    Check.that(
        min <= max,
        () -> "The lower bound of the source range (" + min
            + ") is greater than the upper bound (" + max + ")");

    return scale(value, minRangeValue, maxRangeValue, min, max);
  }

  /** Min-max scaling of a value, whose bounds have already been checked. */
  private static double scale(
      double value, double minRangeValue, double maxRangeValue, double min, double max) {
    // Handle the case where all source values are identical (min == max)
    if (max == min) {
      // Return the midpoint of the target range
      return (minRangeValue + maxRangeValue) / 2.0;
    }

    // Standard min-max normalization formula with range scaling
    return minRangeValue + (((value - min) * (maxRangeValue - minRangeValue)) / (max - min));
  }

  /**
   * Normalizes a value to the range [0, 1] using min-max scaling.
   * This is a convenience method equivalent to calling
   * {@link #normalize(double, double, double, double, double)} with target range [0, 1].
   *
   * <p>Example:
   * <pre>{@code
   * // Normalize 75 from [0, 100] to [0, 1] -> 0.75
   * double n = normalize(75.0, 0.0, 100.0);
   * }</pre>
   *
   * @param value the value to normalize
   * @param min the minimum value in the source range (inclusive)
   * @param max the maximum value in the source range (inclusive)
   * @return the normalized value in the range [0, 1]
   * @throws org.uma.jmetal.util.errorchecking.exception.InvalidConditionException if min > max
   * @see #normalize(double, double, double, double, double)
   */
  public static double normalize(double value, double min, double max) {
    return normalize(value, 0.0, 1.0, min, max);
  }

  /**
   * Normalizes each column of a matrix to the range [0, 1], using the minimum and maximum values of
   * the column. The values of a column whose values are all equal are mapped to 0.5. The matrix is
   * not modified.
   *
   * @param matrix the matrix, with at least one row and one column, rows of the same length and
   *     finite values
   * @return a new matrix with the normalized values
   * @throws org.uma.jmetal.util.errorchecking.exception.NullParameterException if the matrix is null
   * @throws org.uma.jmetal.util.errorchecking.exception.InvalidConditionException if the matrix is
   *     empty, has no columns, has rows of different lengths or has a NaN or infinite value
   */
  public static double[][] normalize(double[][] matrix) {
    checkMatrix(matrix);

    return scaleColumns(matrix, columnMinimums(matrix), columnMaximums(matrix));
  }

  /**
   * Normalizes each column of a matrix to the range [0, 1], using given minimum and maximum values
   * for each column (for example, those of a reference front). The values of a column whose bounds
   * are equal are mapped to 0.5, and a value out of its bounds is mapped out of [0, 1]. The matrix
   * is not modified.
   *
   * @param matrix the matrix, with at least one row and one column, rows of the same length and
   *     finite values
   * @param minRangeValue the minimum value of each column
   * @param maxRangeValue the maximum value of each column, not lower than the minimum
   * @return a new matrix with the normalized values
   * @throws org.uma.jmetal.util.errorchecking.exception.NullParameterException if any argument is
   *     null
   * @throws org.uma.jmetal.util.errorchecking.exception.InvalidConditionException if the matrix is
   *     empty, has no columns, has rows of different lengths or has a NaN or infinite value; if
   *     the bounds are not as many as the columns or are not finite; or if a minimum is greater
   *     than its maximum
   */
  public static double[][] normalize(
      double[][] matrix, double[] minRangeValue, double[] maxRangeValue) {
    checkMatrix(matrix);
    Check.notNull(minRangeValue, "minRangeValue");
    Check.notNull(maxRangeValue, "maxRangeValue");
    checkBounds(minRangeValue, "minRangeValue", matrix[0].length);
    checkBounds(maxRangeValue, "maxRangeValue", matrix[0].length);
    for (int j = 0; j < matrix[0].length; j++) {
      int column = j;
      Check.that(
          minRangeValue[j] <= maxRangeValue[j],
          () -> "The minimum of the column " + column + " (" + minRangeValue[column]
              + ") is greater than its maximum (" + maxRangeValue[column] + ")");
    }

    return scaleColumns(matrix, minRangeValue, maxRangeValue);
  }

  private static double[][] scaleColumns(double[][] matrix, double[] min, double[] max) {
    double[][] normalizedMatrix = new double[matrix.length][matrix[0].length];

    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[0].length; j++) {
        normalizedMatrix[i][j] = scale(matrix[i][j], 0.0, 1.0, min[j], max[j]);
      }
    }

    return normalizedMatrix;
  }

  /** Checks that a matrix has at least one row and one column, rectangular, with finite values. */
  private static void checkMatrix(double[][] matrix) {
    Check.notNull(matrix, "matrix");
    Check.that(matrix.length > 0, "The matrix has no rows");
    Check.notNull(matrix[0], "matrix[0]");
    Check.that(matrix[0].length > 0, "The matrix has no columns");

    int columns = matrix[0].length;
    for (int i = 0; i < matrix.length; i++) {
      int row = i;
      Check.notNull(matrix[i], "a row of the matrix");
      Check.that(
          matrix[i].length == columns,
          () -> "The row " + row + " has " + matrix[row].length + " values, but the first one has "
              + columns);
      for (int j = 0; j < columns; j++) {
        int column = j;
        Check.that(
            Double.isFinite(matrix[i][j]),
            () -> "The value of the row " + row + " and column " + column + " is not finite: "
                + matrix[row][column]);
      }
    }
  }

  private static void checkBounds(double[] bounds, String name, int columns) {
    Check.that(
        bounds.length == columns,
        () -> "The array " + name + " has " + bounds.length + " values, but the matrix has "
            + columns + " columns");
    for (int j = 0; j < bounds.length; j++) {
      int column = j;
      Check.that(
          Double.isFinite(bounds[j]),
          () -> "The value " + column + " of " + name + " is not finite: " + bounds[column]);
    }
  }

  /**
   * Normalizes a vector of double values to the range [0, 1] using the specified min and max values.
   * All values in the vector are normalized using the same min and max values.
   *
   * <p>Example:
   * <pre>{@code
   * double[] vector = {10, 20, 30, 40};
   * // Normalize all values using min=0, max=50
   * double[] normalized = getNormalizedVector(vector, 0, 50);
   * }</pre>
   *
   * @param vector the input vector to normalize (must not be null)
   * @param min the minimum value to use for normalization
   * @param max the maximum value to use for normalization
   * @return a new array containing the normalized values
   * @throws org.uma.jmetal.util.errorchecking.exception.NullParameterException if the vector is
   *     null
   * @throws org.uma.jmetal.util.errorchecking.exception.InvalidConditionException if min > max
   */
  public static double[] getNormalizedVector(double[] vector, double min, double max) {
    Check.notNull(vector, "vector");

    double[] normalizedVector = new double[vector.length];
    IntStream.range(0, vector.length)
            .forEach(i -> normalizedVector[i] = normalize(vector[i], min, max));

    return normalizedVector;
  }

  /**
   * Computes the minimum value for each column in a matrix.
   *
   * <p>Example:
   * <pre>{@code
   * double[][] matrix = {
   *   {1.0, 4.0},
   *   {2.0, 3.0},
   *   {0.5, 5.0}
   * };
   * // Returns [0.5, 3.0]
   * double[] mins = getMinValuesOfTheColumnsOfAMatrix(matrix);
   * }</pre>
   *
   * @param matrix the input matrix, with at least one row and one column, rows of the same length
   *     and finite values
   * @return an array containing the minimum value of each column
   * @throws org.uma.jmetal.util.errorchecking.exception.NullParameterException if the matrix is null
   * @throws org.uma.jmetal.util.errorchecking.exception.InvalidConditionException if the matrix is
   *     empty, has no columns, has rows of different lengths or has a NaN or infinite value
   */
  public static double[] getMinValuesOfTheColumnsOfAMatrix(double[][] matrix) {
    checkMatrix(matrix);

    return columnMinimums(matrix);
  }

  private static double[] columnMinimums(double[][] matrix) {
    int rowLength = matrix[0].length ;
    double[] minValues = new double[rowLength] ;

    Arrays.fill(minValues, Double.MAX_VALUE);

    for (int j = 0; j < rowLength; j++) {
      for (double[] values : matrix) {
        if (values[j] < minValues[j]) {
          minValues[j] = values[j];
        }
      }
    }

    return minValues ;
  }

  /**
   * Computes the maximum value for each column in a matrix.
   *
   * <p>Example:
   * <pre>{@code
   * double[][] matrix = {
   *   {1.0, 4.0},
   *   {2.0, 3.0},
   *   {0.5, 5.0}
   * };
   * // Returns [2.0, 5.0]
   * double[] maxs = getMaxValuesOfTheColumnsOfAMatrix(matrix);
   * }</pre>
   *
   * @param matrix the input matrix, with at least one row and one column, rows of the same length
   *     and finite values
   * @return an array containing the maximum value of each column
   * @throws org.uma.jmetal.util.errorchecking.exception.NullParameterException if the matrix is null
   * @throws org.uma.jmetal.util.errorchecking.exception.InvalidConditionException if the matrix is
   *     empty, has no columns, has rows of different lengths or has a NaN or infinite value
   */
  public static double[] getMaxValuesOfTheColumnsOfAMatrix(double[][] matrix) {
    checkMatrix(matrix);

    return columnMaximums(matrix);
  }

  private static double[] columnMaximums(double[][] matrix) {
    int rowLength = matrix[0].length ;
    double[] maxValues = new double[rowLength] ;

    // Not Double.MIN_VALUE, which is the smallest positive double: with it, the maximum of a column
    // whose values are all negative would be (almost) zero
    Arrays.fill(maxValues, -Double.MAX_VALUE);

    for (int j = 0; j < rowLength; j++) {
      for (double[] values : matrix) {
        if (values[j] > maxValues[j]) {
          maxValues[j] = values[j];
        }
      }
    }

    return maxValues ;
  }
}
