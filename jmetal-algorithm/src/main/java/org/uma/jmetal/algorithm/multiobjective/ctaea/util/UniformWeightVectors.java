package org.uma.jmetal.algorithm.multiobjective.ctaea.util;

import java.util.List;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.referencepoint.ReferencePointGenerator;

/**
 * Weight vectors of C-TAEA, generated as the {@code UniformPoint(N, M)} function of PlatEMO (its
 * default method, the normal-boundary intersection of Das and Dennis with two layers).
 *
 * <p>The outer layer uses the largest number of divisions H1 that gives at most N vectors. If H1
 * is smaller than the number of objectives M, an inner layer is added with the largest number of
 * divisions H2 such that both layers have at most N vectors; the inner layer is shrunk by half
 * towards the center of the simplex. Finally, every component is raised to at least
 * {@code 1e-6}, so that the Tchebycheff function never divides by zero. Hence the number of
 * vectors may be smaller than N, and C-TAEA uses it as its population size.
 *
 * <p>The layers are generated with {@link ReferencePointGenerator}, which yields the vectors in
 * the same order as PlatEMO.
 */
public final class UniformWeightVectors {
  /** Lower bound of the components of the weight vectors, as in PlatEMO. */
  public static final double MINIMUM_COMPONENT = 1e-6;

  private UniformWeightVectors() {}

  /**
   * Generates the weight vectors.
   *
   * @param numberOfVectors the requested number of vectors N (at least 1)
   * @param numberOfObjectives the number of objectives M (at least 2)
   * @return the weight vectors, at most N unless N is smaller than M (one division per objective
   *     gives M vectors)
   */
  public static double[][] generate(int numberOfVectors, int numberOfObjectives) {
    Check.that(numberOfVectors >= 1, "The number of weight vectors must be at least 1");
    Check.that(numberOfObjectives >= 2, "The number of objectives must be at least 2");

    int outerDivisions = 1;
    while (count(numberOfObjectives, outerDivisions + 1) <= numberOfVectors) {
      outerDivisions++;
    }

    int innerDivisions = 0;
    if (outerDivisions < numberOfObjectives) {
      int outerCount = count(numberOfObjectives, outerDivisions);
      while (outerCount + count(numberOfObjectives, innerDivisions + 1) <= numberOfVectors) {
        innerDivisions++;
      }
    }

    List<double[]> vectors =
        innerDivisions > 0
            ? ReferencePointGenerator.generateTwoLayers(
                numberOfObjectives, outerDivisions, innerDivisions)
            : ReferencePointGenerator.generateSingleLayer(numberOfObjectives, outerDivisions);

    double[][] weights = vectors.toArray(new double[0][]);
    for (double[] weight : weights) {
      for (int j = 0; j < weight.length; j++) {
        weight[j] = Math.max(weight[j], MINIMUM_COMPONENT);
      }
    }
    return weights;
  }

  /** Number of vectors of a layer with the given divisions: C(H + M - 1, M - 1). */
  private static int count(int numberOfObjectives, int divisions) {
    return ReferencePointGenerator.calculateNumberOfReferencePoints(numberOfObjectives, divisions);
  }
}
