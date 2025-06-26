package org.uma.jmetal.util;

import java.util.Arrays;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

/**
 * Utility class for computing the distance of each row in a matrix to its K-th nearest neighbor
 * using Euclidean distance, with optional normalization.
 *
 * Code created with an AI generative tool
 */
public class KNearestDistanceCalculator {
  private final int k;
  private final boolean normalize;
  private static final double EPSILON = 1e-10;

  /**
   * Creates a new KNearestDistanceCalculator.
   *
   * @param k         The number of nearest neighbors to consider (must be positive and less than the number of rows - 1)
   * @param normalize Whether to normalize the input matrix before computing distances
   * @throws IllegalArgumentException if k is not positive or if k is greater than or equal to the number of rows - 1
   */
  public KNearestDistanceCalculator(int k, boolean normalize) {
    if (k <= 0) {
      throw new IllegalArgumentException("k must be positive");
    }
    this.k = k;
    this.normalize = normalize;
  }

  /**
   * Computes the distance of each row to its K-th nearest neighbor.
   *
   * @param matrix The input matrix (n x m) where n is the number of rows and m is the number of features
   * @return An array where the i-th element is the distance of the i-th row to its K-th nearest neighbor
   * @throws IllegalArgumentException if the input matrix is null, empty, or contains rows of different lengths
   * @throws IllegalStateException if k is greater than or equal to the number of rows - 1
   */
  public double[] compute(double[][] matrix) {
    validateInput(matrix);

    double[][] data = normalize ? normalizeMatrix(matrix) : matrix;
    int n = data.length;

    if (k >= n) {
      throw new IllegalStateException(
              String.format("k (%d) must be less than the number of rows - 1 (%d)", k, n - 1));
    }

    double[] kthDistances = new double[n];

    for (int i = 0; i < n; i++) {
      // Compute distances from current row to all other rows
      double[] distances = new double[n - 1];
      int idx = 0;
      for (int j = 0; j < n; j++) {
        if (i != j) {
          distances[idx++] = euclideanDistance(data[i], data[j]);
        }
      }

      // Sort distances and get the k-th smallest (0-based index is k-1)
      Arrays.sort(distances);
      kthDistances[i] = distances[k - 1];
    }

    return kthDistances;
  }

  /**
   * Validates the input matrix.
   */
  private void validateInput(double[][] matrix) {
    if (matrix == null || matrix.length == 0) {
      throw new IllegalArgumentException("Input matrix cannot be null or empty");
    }

    int cols = matrix[0].length;
    for (double[] row : matrix) {
      if (row == null || row.length != cols) {
        throw new IllegalArgumentException("All rows must have the same number of columns");
      }
    }
  }

  /**
   * Normalizes the input matrix column-wise to [0, 1] range.
   */
  private double[][] normalizeMatrix(double[][] matrix) {
    int rows = matrix.length;
    int cols = matrix[0].length;
    double[][] normalized = new double[rows][cols];

    // Find min and max for each column
    double[] min = new double[cols];
    double[] max = new double[cols];
    Arrays.fill(min, Double.POSITIVE_INFINITY);
    Arrays.fill(max, Double.NEGATIVE_INFINITY);

    for (double[] row : matrix) {
      for (int j = 0; j < cols; j++) {
        min[j] = Math.min(min[j], row[j]);
        max[j] = Math.max(max[j], row[j]);
      }
    }

    // Normalize each column
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        double range = max[j] - min[j];
        if (range < EPSILON) {
          normalized[i][j] = 0.5; // Handle constant columns
        } else {
          normalized[i][j] = (matrix[i][j] - min[j]) / range;
        }
      }
    }

    return normalized;
  }

  /**
   * Computes the Euclidean distance between two vectors.
   */
  private double euclideanDistance(double[] a, double[] b) {
    double sum = 0.0;
    for (int i = 0; i < a.length; i++) {
      double diff = a[i] - b[i];
      sum += diff * diff;
    }
    return Math.sqrt(sum);
  }
}
