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
   * @throws IllegalArgumentException if min > max or minRangeValue > maxRangeValue
   */
  public static double normalize(
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
   * @throws IllegalArgumentException if min > max
   * @see #normalize(double, double, double, double, double)
   */
  public static double normalize(double value, double min, double max) {
    return normalize(value, 0.0, 1.0, min, max);
  }

  /**
   * Normalize the vectors (rows) of bi-dimensional matrix
   *
   * @param matrix
   * @return A matrix with normalized values for each of its rows
   */
  public static double[][] normalize(double[][] matrix) {
    Check.notNull(matrix);

    double[][] normalizedMatrix = new double[matrix.length][matrix[0].length];

    double[] minValue = getMinValuesOfTheColumnsOfAMatrix(matrix) ;
    double[] maxValue = getMaxValuesOfTheColumnsOfAMatrix(matrix) ;

    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[0].length; j++) {
        normalizedMatrix[i][j] = NormalizeUtils.normalize(matrix[i][j], minValue[j], maxValue[j]) ;
      }
    }

    return normalizedMatrix;
  }

  /**
   * Normalize the vectors (rows) of bi-dimensional matrix
   *
   * @param matrix
   * @return A matrix with normalized values for each of its rows
   */
  public static double[][] normalize(double[][] matrix, double[] minRangeValue, double[] maxRangeValue) {
    Check.notNull(matrix);

    double[][] normalizedMatrix = new double[matrix.length][matrix[0].length];

    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[0].length; j++) {
        normalizedMatrix[i][j] = normalize(matrix[i][j], minRangeValue[j], maxRangeValue[j]) ;
      }
    }

    return normalizedMatrix;
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
   * @throws IllegalArgumentException if vector is null or min > max
   */
  public static double[] getNormalizedVector(double[] vector, double min, double max) {
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
   * @param matrix the input matrix (must not be null or empty)
   * @return an array containing the minimum value of each column
   * @throws IllegalArgumentException if matrix is null, empty, or contains rows of different lengths
   */
  public static double[]getMinValuesOfTheColumnsOfAMatrix(double[][] matrix) {
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
   * @param matrix the input matrix (must not be null or empty)
   * @return an array containing the maximum value of each column
   * @throws IllegalArgumentException if matrix is null, empty, or contains rows of different lengths
   */
  public static double[]getMaxValuesOfTheColumnsOfAMatrix(double[][] matrix) {
    int rowLength = matrix[0].length ;
    double[] maxValues = new double[rowLength] ;

    Arrays.fill(maxValues, Double.MIN_VALUE);

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
