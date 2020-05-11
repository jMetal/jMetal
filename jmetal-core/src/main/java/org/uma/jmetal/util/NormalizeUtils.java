package org.uma.jmetal.util;

import org.uma.jmetal.util.checking.Check;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Class responsible for normalizing values
 *
 * @author Thiago Ferreira
 * @version 1.0.0
 * @since 2018-12-16
 */
public class NormalizeUtils {

  private NormalizeUtils() throws InstantiationException {
    throw new InstantiationException("Instances of this type are forbidden");
  }

  /**
   * It normalizes a {@code value} in a range [{@code a}, {@code b}] given a {@code min} and {@code
   * max} value. The equation used here were based on the following links:
   *
   * <ul>
   *   <li>{@link https://stats.stackexchange.com/a/281165}
   *   <li>{@link https://stats.stackexchange.com/a/178629}
   *   <li>{@link https://en.wikipedia.org/wiki/Normalization_(statistics)}
   *   <li>{@link https://en.wikipedia.org/wiki/Feature_scaling}
   * </ul>
   *
   * @param value value number to be normalized
   * @param minRangeValue the minimum value for the range
   * @param maxRangeValue the maximum value for the range
   * @param min minimum value that {@code value} can take on
   * @param max maximum value that {@code value} can take on
   * @return the normalized number
   */
  public static double normalize(
          double value, double minRangeValue, double maxRangeValue, double min, double max) {
    Check.that(max != min, "Max minus min should not be zero");

    return minRangeValue + (((value - min) * (maxRangeValue - minRangeValue)) / (max - min));
  }

  /**
   * It normalizes a {@code value} in [0,1] given a {@code min} and {@code max} value.
   *
   * @param value number to be normalized
   * @param min minimum value that {@code value} can take on
   * @param max maximum value that {@code value} can take on
   * @return the normalized number
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
    Check.isNotNull(matrix);

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
   * Normalizes a vector of double values
   *
   * @param vector
   * @return The normalized vector
   */
  public static double[] getNormalizedVector(double[] vector, double min, double max) {
    double[] normalizedVector = new double[vector.length];

    IntStream.range(0, vector.length)
            .forEach(i -> normalizedVector[i] = normalize(vector[i], min, max));

    return normalizedVector;
  }

  /**
   * Returns a vector with the minimum values of the columns of a matrix
   *
   * @param matrix
   * @return
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
   * Returns a vector with the maximum values of the columns of a matrix
   *
   * @param matrix
   * @return
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
