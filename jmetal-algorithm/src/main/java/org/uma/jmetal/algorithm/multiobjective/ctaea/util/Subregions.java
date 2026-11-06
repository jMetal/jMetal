package org.uma.jmetal.algorithm.multiobjective.ctaea.util;

import java.util.Arrays;

/**
 * Operations on the subregions of C-TAEA, each one defined by a weight vector: the association
 * of a vector of objectives with a subregion and the Tchebycheff function, transcribed from
 * PlatEMO.
 *
 * <p>Ties are broken as MATLAB's {@code max} and {@code min} do: the first index wins.
 */
public final class Subregions {
  private Subregions() {}

  /**
   * Returns the index of the weight vector with the largest cosine similarity with a point (the
   * smallest angle), as {@code max(1 - pdist2(point, W, 'cosine'), [], 2)} in PlatEMO. A point at
   * the origin has no angle (its similarities are {@code NaN}), so it is associated with the
   * first vector.
   *
   * @param point the vector of objectives (translated or normalized if needed by the caller)
   * @param weights the weight vectors
   * @return the index of the associated weight vector
   */
  public static int associate(double[] point, double[][] weights) {
    double pointNorm = norm(point);
    int best = 0;
    double bestSimilarity = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < weights.length; i++) {
      double similarity = dot(point, weights[i]) / (pointNorm * norm(weights[i]));
      if (similarity > bestSimilarity) {
        bestSimilarity = similarity;
        best = i;
      }
    }
    return best;
  }

  /**
   * Associates every row of a matrix with a weight vector (see {@link #associate(double[],
   * double[][])}).
   *
   * @param points the vectors of objectives (rows)
   * @param weights the weight vectors
   * @return the index of the weight vector associated with each row
   */
  public static int[] associate(double[][] points, double[][] weights) {
    int[] regions = new int[points.length];
    for (int i = 0; i < points.length; i++) {
      regions[i] = associate(points[i], weights);
    }
    return regions;
  }

  /**
   * Returns the ideal point of a set of vectors: the minimum of every component.
   *
   * @param points the vectors (rows), at least one
   * @return the ideal point
   */
  public static double[] idealPoint(double[][] points) {
    double[] ideal = Arrays.copyOf(points[0], points[0].length);
    for (double[] point : points) {
      for (int j = 0; j < ideal.length; j++) {
        ideal[j] = Math.min(ideal[j], point[j]);
      }
    }
    return ideal;
  }

  /**
   * Returns the nadir point of a set of vectors: the maximum of every component.
   *
   * @param points the vectors (rows), at least one
   * @return the nadir point
   */
  public static double[] nadirPoint(double[][] points) {
    double[] nadir = Arrays.copyOf(points[0], points[0].length);
    for (double[] point : points) {
      for (int j = 0; j < nadir.length; j++) {
        nadir[j] = Math.max(nadir[j], point[j]);
      }
    }
    return nadir;
  }

  /**
   * Returns the Tchebycheff value {@code max_j |f_j - z_j| / w_j} of a vector of objectives, as
   * in PlatEMO.
   *
   * @param point the vector of objectives f
   * @param idealPoint the ideal point z
   * @param weight the weight vector w (all its components are positive)
   * @return the Tchebycheff value
   */
  public static double tchebycheff(double[] point, double[] idealPoint, double[] weight) {
    double value = Double.NEGATIVE_INFINITY;
    for (int j = 0; j < point.length; j++) {
      value = Math.max(value, Math.abs(point[j] - idealPoint[j]) / weight[j]);
    }
    return value;
  }

  /**
   * Returns the Tchebycheff values of a set of vectors, each one with the weight vector of its
   * subregion and the ideal point of the set, as PlatEMO does in C-TAEA.
   *
   * @param points the vectors of objectives (rows), at least one
   * @param weights the weight vectors
   * @return the Tchebycheff value of each row
   */
  public static double[] tchebycheffInOwnSubregion(double[][] points, double[][] weights) {
    int[] regions = associate(points, weights);
    double[] ideal = idealPoint(points);
    double[] values = new double[points.length];
    for (int i = 0; i < points.length; i++) {
      values[i] = tchebycheff(points[i], ideal, weights[regions[i]]);
    }
    return values;
  }

  /**
   * Returns the index of the largest value; the first one in case of a tie.
   *
   * @param values the values, at least one
   * @return the index of the first maximum
   */
  public static int indexOfMaximum(double[] values) {
    int best = 0;
    for (int i = 1; i < values.length; i++) {
      if (values[i] > values[best]) {
        best = i;
      }
    }
    return best;
  }

  /**
   * Returns the index of the smallest value; the first one in case of a tie.
   *
   * @param values the values, at least one
   * @return the index of the first minimum
   */
  public static int indexOfMinimum(double[] values) {
    int best = 0;
    for (int i = 1; i < values.length; i++) {
      if (values[i] < values[best]) {
        best = i;
      }
    }
    return best;
  }

  private static double dot(double[] a, double[] b) {
    double sum = 0.0;
    for (int j = 0; j < a.length; j++) {
      sum += a[j] * b[j];
    }
    return sum;
  }

  private static double norm(double[] a) {
    return Math.sqrt(dot(a, a));
  }
}
