package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;

/** Numerical operations shared by the reference-vector selection strategies. */
final class RVEAGeometry {
  static final double MIN_ANGLE = 1.0e-12;

  private RVEAGeometry() {}

  static double norm(double[] vector) {
    double length = 0.0;
    for (double coordinate : vector) {
      length = Math.hypot(length, coordinate);
    }
    return length;
  }

  static double[] unit(double[] vector) {
    double maximum = Arrays.stream(vector).map(Math::abs).max().orElse(0.0);
    Check.that(maximum > 0.0 && Double.isFinite(maximum), "A direction must be finite and nonzero");
    double[] result = Arrays.stream(vector).map(value -> value / maximum).toArray();
    double length = norm(result);
    for (int i = 0; i < result.length; i++) {
      result[i] /= length;
    }
    return result;
  }

  static double angle(double[] first, double[] second) {
    double firstNorm = norm(first);
    double secondNorm = norm(second);
    if (firstNorm == 0.0 || secondNorm == 0.0) {
      return 0.0;
    }
    double dot = 0.0;
    for (int i = 0; i < first.length; i++) {
      dot += (first[i] / firstNorm) * (second[i] / secondNorm);
    }
    return Math.acos(Math.max(-1.0, Math.min(1.0, dot)));
  }

  static double[][] copy(double[][] vectors) {
    return Arrays.stream(vectors).map(double[]::clone).toArray(double[][]::new);
  }

  static double[][] directions(List<double[]> vectors, int objectives) {
    Check.notNull(vectors);
    Check.that(!vectors.isEmpty(), "At least one reference vector is required");
    for (double[] vector : vectors) {
      Check.notNull(vector);
      Check.that(vector.length == objectives, "Reference vector dimension must match the problem");
      Check.that(
          Arrays.stream(vector).allMatch(v -> Double.isFinite(v) && v >= 0.0),
          "Reference vectors must have finite, nonnegative coordinates");
    }
    return vectors.stream().map(RVEAGeometry::unit).toArray(double[][]::new);
  }

  static double[][] translate(List<? extends Solution<?>> population) {
    int objectives = population.getFirst().objectives().length;
    double[] minimum = new double[objectives];
    Arrays.fill(minimum, Double.POSITIVE_INFINITY);
    for (Solution<?> solution : population) {
      for (int j = 0; j < objectives; j++) {
        minimum[j] = Math.min(minimum[j], solution.objectives()[j]);
      }
    }
    double[][] result = new double[population.size()][objectives];
    for (int i = 0; i < population.size(); i++) {
      for (int j = 0; j < objectives; j++) {
        result[i][j] = population.get(i).objectives()[j] - minimum[j];
        Check.that(Double.isFinite(result[i][j]), "The objective range must be finite");
      }
    }
    return result;
  }

  static double[] ranges(double[][] translated) {
    double[] ranges = new double[translated[0].length];
    for (double[] row : translated) {
      for (int j = 0; j < ranges.length; j++) {
        ranges[j] = Math.max(ranges[j], row[j]);
      }
    }
    return ranges;
  }

  static double[][] normalize(double[][] translated) {
    double[] ranges = ranges(translated);
    double[][] result = copy(translated);
    for (double[] row : result) {
      for (int j = 0; j < ranges.length; j++) {
        row[j] = ranges[j] == 0.0 ? 0.0 : row[j] / ranges[j];
      }
    }
    return result;
  }

  static int nearest(double[] point, double[][] vectors) {
    int nearest = 0;
    double minimum = Double.POSITIVE_INFINITY;
    for (int i = 0; i < vectors.length; i++) {
      double angle = angle(point, vectors[i]);
      if (angle < minimum) {
        minimum = angle;
        nearest = i;
      }
    }
    return nearest;
  }

  static boolean[] active(double[][] points, double[][] vectors) {
    boolean[] active = new boolean[vectors.length];
    for (double[] point : points) {
      active[nearest(point, vectors)] = true;
    }
    return active;
  }

  static double[] gamma(double[][] vectors) {
    double[] result = new double[vectors.length];
    Arrays.fill(result, Math.PI / 2.0);
    for (int i = 0; i < vectors.length; i++) {
      for (int j = i + 1; j < vectors.length; j++) {
        double angle = Math.max(MIN_ANGLE, angle(vectors[i], vectors[j]));
        result[i] = Math.min(result[i], angle);
        result[j] = Math.min(result[j], angle);
      }
    }
    return result;
  }
}
