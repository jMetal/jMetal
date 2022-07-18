package org.uma.jmetal.util.distance.impl;

import org.uma.jmetal.util.distance.Distance;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.stream.IntStream;

/**
 * Class for calculating the dominance distance between two vectors
 *
 * @author <antonio@lcc.uma.es>
 */
public class DominanceDistanceBetweenVectors implements Distance<double[], double[]> {

  @Override
  public double compute(double[] vector1, double[] vector2) {
    Check.notNull(vector1);
    Check.notNull(vector2);
    Check.that(vector1.length == vector2.length, "The vectors have different" +
            "dimension: " + vector1.length + " and " + vector2.length);

    double distance = IntStream.range(0, vector1.length).mapToDouble(i -> Math.max(vector2[i] - vector1[i], 0.0)).map(max -> Math.pow(max, 2)).sum();

      return Math.sqrt(distance);
  }
}
