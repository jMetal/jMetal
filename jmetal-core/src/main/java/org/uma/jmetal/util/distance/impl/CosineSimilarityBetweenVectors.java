package org.uma.jmetal.util.distance.impl;

import org.uma.jmetal.util.distance.Distance;

/**
 * Class for calculating the cosine similarity between two vectors.
 *
 * @author
 */
public class CosineSimilarityBetweenVectors implements Distance<double[], double[]> {

  private final double[] referencePoint;

  public CosineSimilarityBetweenVectors(double[] referencePoint) {
    this.referencePoint = referencePoint;
  }

  @Override
  public double compute(double[] vector1, double[] vector2) {
    double sum = 0.0;

    for (int i = 0; i < vector1.length; i++) {
      sum += (vector1[i] - referencePoint[i]) * (vector2[i] - referencePoint[i]);
    }

    double norms = sumOfDistancesToIdealPoint(vector1) * sumOfDistancesToIdealPoint(vector2);

    // The cosine of a vector that is the reference point is not defined: it is taken as zero
    // (orthogonal), so that it does not become a NaN that no comparison can order
    return norms > 0.0 ? sum / norms : 0.0;
  }

  private double sumOfDistancesToIdealPoint(double[] vector) {
    double sum = 0.0;

    for (int i = 0; i < vector.length; i++) {
      sum += Math.pow(vector[i] - referencePoint[i], 2.0);
    }

    return Math.sqrt(sum);
  }
}
