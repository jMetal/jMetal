package org.uma.jmetal.util;

import org.uma.jmetal.util.errorchecking.Check;

/**
 * Utility class for reference point operations in multi-objective optimization. Provides methods to
 * convert between reference points and reference fronts.
 */
public class ReferencePointUtils {

  /**
   * Creates a reference front from a reference point. The reference front will have n points where
   * n is the dimension of the reference point, with each point having all objectives at 0 except
   * for one objective which is set to the reference point's corresponding value.
   *
   * @param referencePoint The reference point
   * @return A reference front generated from the reference point
   * @throws IllegalArgumentException if referencePoint is null or empty
   */
  public static double[][] createReferenceFrontFromReferencePoint(double[] referencePoint) {
    Check.notNull(referencePoint);
    Check.that(referencePoint.length > 0, "The reference point cannot be empty");

    int numberOfObjectives = referencePoint.length;
    double[][] referenceFront = new double[numberOfObjectives][numberOfObjectives];

    for (int i = 0; i < numberOfObjectives; i++) {
      double[] point = new double[numberOfObjectives];
      for (int j = 0; j < numberOfObjectives; j++) {
        point[j] = (j == i) ? referencePoint[i] : 0.0;
      }
      referenceFront[i] = point;
    }

    return referenceFront;
  }

  /**
   * Extracts a reference point from a reference front. The reference point is created by taking the
   * maximum value in each objective dimension across all points in the front.
   *
   * @param referenceFront The reference front
   * @return A reference point derived from the reference front
   * @throws IllegalArgumentException if referenceFront is null, empty, or contains null points
   */
  public static double[] extractReferencePointFromReferenceFront(double[][] referenceFront) {
    Check.notNull(referenceFront);
    Check.that(referenceFront.length > 0, "The reference front cannot be empty");

    int numberOfObjectives = referenceFront[0].length;
    double[] referencePoint = new double[numberOfObjectives];

    // Initialize with minimum possible values
    for (int i = 0; i < numberOfObjectives; i++) {
      referencePoint[i] = Double.NEGATIVE_INFINITY;
    }

    // Find maximum in each objective
    for (double[] point : referenceFront) {
      if (point == null) {
        throw new IllegalArgumentException("The reference front contains null points");
      }
      if (point.length != numberOfObjectives) {
        throw new IllegalArgumentException(
            "All points in the reference front must have the same number of objectives");
      }

      for (int i = 0; i < numberOfObjectives; i++) {
        if (point[i] > referencePoint[i]) {
          referencePoint[i] = point[i];
        }
      }
    }

    return referencePoint;
  }
}
