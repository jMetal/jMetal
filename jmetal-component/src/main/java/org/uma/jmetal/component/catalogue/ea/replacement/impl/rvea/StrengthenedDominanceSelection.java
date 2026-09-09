package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.solution.Solution;

/** First-front selection with Tian et al.'s strengthened dominance relation (TEVC, 2019). */
final class StrengthenedDominanceSelection {
  private StrengthenedDominanceSelection() {}

  static <S extends Solution<?>> List<S> firstFront(List<S> population) {
    if (population.size() < 2) {
      return List.copyOf(population);
    }
    double[][] points = RVEAGeometry.normalize(RVEAGeometry.translate(population));
    double[] convergence =
        Arrays.stream(points).mapToDouble(row -> Arrays.stream(row).sum()).toArray();
    double[][] angles = new double[points.length][points.length];
    double[] nearest = new double[points.length];
    Arrays.fill(nearest, Math.PI / 2.0);
    for (int i = 0; i < points.length; i++) {
      for (int j = i + 1; j < points.length; j++) {
        angles[i][j] = RVEAGeometry.angle(points[i], points[j]);
        angles[j][i] = angles[i][j];
        nearest[i] = Math.min(nearest[i], angles[i][j]);
        nearest[j] = Math.min(nearest[j], angles[i][j]);
      }
    }
    double[] distinctAngles = Arrays.stream(nearest).distinct().sorted().toArray();
    int median = Math.min((points.length + 1) / 2, distinctAngles.length) - 1;
    double threshold = Math.max(RVEAGeometry.MIN_ANGLE, distinctAngles[median]);
    boolean[] dominated = new boolean[points.length];
    for (int i = 0; i < points.length; i++) {
      for (int j = i + 1; j < points.length; j++) {
        double penalty = Math.max(1.0, angles[i][j] / threshold);
        if (convergence[i] * penalty < convergence[j]) {
          dominated[j] = true;
        } else if (convergence[j] * penalty < convergence[i]) {
          dominated[i] = true;
        }
      }
    }
    return IntStream.range(0, points.length)
        .filter(i -> !dominated[i])
        .mapToObj(population::get)
        .toList();
  }
}
