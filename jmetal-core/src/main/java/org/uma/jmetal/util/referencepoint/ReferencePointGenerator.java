package org.uma.jmetal.util.referencepoint;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Utility class for generating reference points for NSGA-III.
 *
 * <p>
 * Implements Das and Dennis's systematic approach for generating uniformly
 * distributed reference points on a unit simplex.
 *
 * @author Antonio J. Nebro
 */
public class ReferencePointGenerator {

  /**
   * Generates a single layer of reference points.
   *
   * @param numberOfObjectives Number of objectives (M)
   * @param numberOfDivisions  Number of divisions (H)
   * @return List of reference points, each as a double array
   */
  public static List<double[]> generateSingleLayer(int numberOfObjectives, int numberOfDivisions) {
    Check.that(numberOfObjectives >= 2, "Number of objectives must be >= 2");
    Check.that(numberOfDivisions >= 1, "Number of divisions must be >= 1");

    List<double[]> referencePoints = new ArrayList<>();
    double[] refPoint = new double[numberOfObjectives];
    generateRecursive(referencePoints, refPoint, numberOfObjectives, numberOfDivisions, numberOfDivisions, 0);
    return referencePoints;
  }

  /**
   * Generates two layers of reference points for many-objective problems.
   *
   * <p>
   * As recommended in the NSGA-III paper for problems with more than 5
   * objectives.
   *
   * @param numberOfObjectives Number of objectives (M)
   * @param outerDivisions     Divisions for outer (boundary) layer
   * @param innerDivisions     Divisions for inner layer
   * @return Combined list of reference points from both layers
   */
  public static List<double[]> generateTwoLayers(int numberOfObjectives, int outerDivisions, int innerDivisions) {
    Check.that(numberOfObjectives >= 2, "Number of objectives must be >= 2");
    Check.that(outerDivisions >= 1, "Outer divisions must be >= 1");
    Check.that(innerDivisions >= 1, "Inner divisions must be >= 1");

    // Generate outer layer
    List<double[]> referencePoints = generateSingleLayer(numberOfObjectives, outerDivisions);

    // Generate inner layer
    List<double[]> innerLayer = generateSingleLayer(numberOfObjectives, innerDivisions);

    // Shrink inner layer points towards center (0.5, 0.5, ..., 0.5)
    double shrinkFactor = 0.5;
    for (double[] point : innerLayer) {
      double sum = 0.0;
      for (int i = 0; i < point.length; i++) {
        point[i] = shrinkFactor + point[i] * shrinkFactor;
        sum += point[i];
      }
      // Normalize to sum to 1
      for (int i = 0; i < point.length; i++) {
        point[i] = point[i] / sum;
      }
    }

    referencePoints.addAll(innerLayer);
    return referencePoints;
  }

  /**
   * Calculates the number of reference points for given objectives and divisions.
   * Formula: C(H + M - 1, M - 1) = (H + M - 1)! / (H! * (M - 1)!)
   */
  public static int calculateNumberOfReferencePoints(int numberOfObjectives, int numberOfDivisions) {
    return binomialCoefficient(numberOfDivisions + numberOfObjectives - 1, numberOfObjectives - 1);
  }

  private static void generateRecursive(
      List<double[]> referencePoints,
      double[] refPoint,
      int numberOfObjectives,
      int left,
      int total,
      int element) {

    if (element == numberOfObjectives - 1) {
      refPoint[element] = (double) left / total;
      referencePoints.add(refPoint.clone());
    } else {
      for (int i = 0; i <= left; i++) {
        refPoint[element] = (double) i / total;
        generateRecursive(referencePoints, refPoint, numberOfObjectives, left - i, total, element + 1);
      }
    }
  }

  private static int binomialCoefficient(int n, int k) {
    if (k > n - k) {
      k = n - k;
    }
    int result = 1;
    for (int i = 0; i < k; i++) {
      result = result * (n - i) / (i + 1);
    }
    return result;
  }
}
