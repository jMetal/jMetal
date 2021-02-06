package org.uma.jmetal.util.distance.impl;

import org.uma.jmetal.util.distance.Distance;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Class for calculating the Euclidean distance between two vectors
 *
 * @author <antonio@lcc.uma.es>
 */
public class EuclideanDistanceBetweenVectors implements Distance<double[], double[]> {

  @Override
  public double compute(double[] vector1, double[] vector2) {
    Check.notNull(vector1);
    Check.notNull(vector2);
    Check.that(vector1.length == vector2.length, "The vectors have different" +
            "dimension: " + vector1.length + " and " + vector2.length);

    double distance = 0.0;

    double diff;
    for (int i = 0; i < vector1.length ; i++){
      diff = vector1[i] - vector2[i];
      distance += diff * diff ;
    }

    return Math.sqrt(distance);
  }
}
