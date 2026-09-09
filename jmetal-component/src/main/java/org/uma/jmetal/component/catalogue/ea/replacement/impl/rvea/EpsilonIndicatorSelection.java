package org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;

/** Additive epsilon indicator fitness and iterative deletion for iRVEA, with kappa = 0.05. */
final class EpsilonIndicatorSelection {
  private static final double KAPPA = 0.05;

  private EpsilonIndicatorSelection() {}

  static <S extends Solution<?>> List<S> select(List<S> population, int size) {
    Check.that(size >= 0, "The requested archive size must be nonnegative");
    if (population.size() <= size) {
      return new ArrayList<>(population);
    }
    double[][] objectives = RVEAGeometry.normalize(RVEAGeometry.translate(population));
    double[][] contributions = contributions(objectives);
    double[] loss = new double[population.size()];
    for (int j = 0; j < loss.length; j++) {
      for (int i = 0; i < loss.length; i++) {
        loss[j] += contributions[i][j];
      }
    }
    boolean[] removed = new boolean[population.size()];
    for (int count = population.size(); count > size; count--) {
      int worst = -1;
      for (int i = 0; i < loss.length; i++) {
        if (!removed[i] && (worst < 0 || loss[i] > loss[worst])) {
          worst = i;
        }
      }
      removed[worst] = true;
      for (int i = 0; i < loss.length; i++) {
        loss[i] -= contributions[worst][i];
      }
    }
    return IntStream.range(0, population.size())
        .filter(i -> !removed[i])
        .mapToObj(population::get)
        .toList();
  }

  private static double[][] contributions(double[][] objectives) {
    int size = objectives.length;
    double[][] indicator = new double[size][size];
    for (int j = 0; j < size; j++) {
      double scale = 0.0;
      for (int i = 0; i < size; i++) {
        indicator[i][j] = epsilon(objectives[i], objectives[j]);
        scale = Math.max(scale, Math.abs(indicator[i][j]));
      }
      double denominator = KAPPA * (scale == 0.0 ? 1.0 : scale);
      for (int i = 0; i < size; i++) {
        indicator[i][j] = i == j ? 0.0 : Math.exp(-indicator[i][j] / denominator);
      }
    }
    return indicator;
  }

  private static double epsilon(double[] first, double[] second) {
    double result = Double.NEGATIVE_INFINITY;
    for (int j = 0; j < first.length; j++) {
      result = Math.max(result, first[j] - second[j]);
    }
    return result;
  }
}
