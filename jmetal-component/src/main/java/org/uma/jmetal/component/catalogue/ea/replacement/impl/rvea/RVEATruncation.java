package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.solution.Solution;

/** Angular population reduction used by RVEA* and iRVEA. */
final class RVEATruncation {
  private RVEATruncation() {}

  static <S extends Solution<?>> List<S> crowded(List<S> population, int size) {
    if (population.size() <= size) {
      return new ArrayList<>(population);
    }
    double[][] points = RVEAGeometry.translate(population);
    double[][] angles = angles(points);
    List<Integer> remaining =
        new ArrayList<>(IntStream.range(0, population.size()).boxed().toList());
    while (remaining.size() > size) {
      int worst = remaining.getFirst();
      double[] closest = neighborAngles(worst, remaining, angles);
      for (int candidate : remaining) {
        double[] neighbors = neighborAngles(candidate, remaining, angles);
        int comparison = Arrays.compare(neighbors, closest);
        if (comparison < 0
            || (comparison == 0
                && RVEAGeometry.norm(points[candidate]) > RVEAGeometry.norm(points[worst]))) {
          worst = candidate;
          closest = neighbors;
        }
      }
      remaining.remove(Integer.valueOf(worst));
    }
    return remaining.stream().map(population::get).toList();
  }

  private static double[] neighborAngles(int index, List<Integer> remaining, double[][] angles) {
    return remaining.stream()
        .filter(other -> other != index)
        .mapToDouble(other -> angles[index][other])
        .sorted()
        .toArray();
  }

  /** Average-linkage angular clustering, keeping the nearest-to-ideal member of each cluster. */
  static <S extends Solution<?>> List<S> cluster(List<S> population, int size) {
    if (size == 0) {
      return List.of();
    }
    if (population.size() <= size) {
      return new ArrayList<>(population);
    }
    double[][] points = RVEAGeometry.translate(population);
    double[][] distances = angles(points);
    int[] counts = new int[population.size()];
    Arrays.fill(counts, 1);
    int[] representative = IntStream.range(0, population.size()).toArray();
    for (int count = population.size(); count > size; count--) {
      int[] pair = closestClusters(distances, counts);
      int first = pair[0];
      int second = pair[1];
      for (int i = 0; i < counts.length; i++) {
        if (counts[i] > 0 && i != first && i != second) {
          double distance =
              (counts[first] * distances[first][i] + counts[second] * distances[second][i])
                  / (counts[first] + counts[second]);
          distances[first][i] = distance;
          distances[i][first] = distance;
        }
      }
      if (RVEAGeometry.norm(points[representative[second]])
          < RVEAGeometry.norm(points[representative[first]])) {
        representative[first] = representative[second];
      }
      counts[first] += counts[second];
      counts[second] = 0;
    }
    return IntStream.range(0, counts.length)
        .filter(i -> counts[i] > 0)
        .map(i -> representative[i])
        .sorted()
        .mapToObj(population::get)
        .toList();
  }

  private static int[] closestClusters(double[][] distances, int[] counts) {
    double minimum = Double.POSITIVE_INFINITY;
    int[] pair = new int[2];
    for (int i = 0; i < counts.length; i++) {
      for (int j = i + 1; j < counts.length; j++) {
        if (counts[i] > 0 && counts[j] > 0 && distances[i][j] < minimum) {
          minimum = distances[i][j];
          pair[0] = i;
          pair[1] = j;
        }
      }
    }
    return pair;
  }

  private static double[][] angles(double[][] points) {
    double[][] result = new double[points.length][points.length];
    for (int i = 0; i < points.length; i++) {
      for (int j = i + 1; j < points.length; j++) {
        result[i][j] = RVEAGeometry.angle(points[i], points[j]);
        result[j][i] = result[i][j];
      }
    }
    return result;
  }
}
