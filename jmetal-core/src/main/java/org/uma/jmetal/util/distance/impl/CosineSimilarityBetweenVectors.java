package org.uma.jmetal.util.distance.impl;

import org.uma.jmetal.util.distance.Distance;

import java.util.stream.IntStream;

/**
 * Class for calculating the cosine similarity between two vectors.
 *
 * @author <antonio@lcc.uma.es>
 */
public class CosineSimilarityBetweenVectors implements Distance<double[], double[]> {

  private double[] referencePoint;

  public CosineSimilarityBetweenVectors(double[] referencePoint) {
    this.referencePoint = referencePoint;
  }

  @Override
  public double compute(double[] vector1, double[] vector2) {
    double sum = 0.0;
    for (int i = 0; i < vector1.length; i++) {
      double v = (vector1[i] - referencePoint[i]) * (vector2[i] - referencePoint[i]);
      sum += v;
    }

    return sum / (sumOfDistancesToIdealPoint(vector1) * sumOfDistancesToIdealPoint(vector2));
  }

  private double sumOfDistancesToIdealPoint(double[] vector) {
    double sum = 0.0;
    for (int i = 0; i < vector.length; i++) {
      double pow = Math.pow(vector[i] - referencePoint[i], 2.0);
      sum += pow;
    }

    return Math.sqrt(sum);
  }
}
