package org.uma.jmetal.util.densityestimator.impl;

import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Shift-Based Density Estimation (SDE) for many-objective optimization.
 *
 * <p>
 * This density estimator implements the technique proposed in:
 * M. Li, S. Yang, and X. Liu, "Shift-Based Density Estimation for Pareto-Based
 * Algorithms in Many-Objective Optimization," IEEE Transactions on Evolutionary
 * Computation, vol. 18, no. 3, pp. 348-365, June 2014.
 *
 * <p>
 * SDE modifies the traditional density estimation by "shifting" other solutions
 * towards the solution being evaluated. This incorporates both convergence and
 * diversity information into a single density metric, making Pareto-based
 * algorithms
 * more effective for many-objective optimization.
 *
 * <p>
 * The key idea: when computing the distance from solution p to solution q,
 * if q is worse than p in any objective, that objective component is shifted to
 * match p's value.
 * This effectively penalizes solutions that are dominated or partially
 * dominated.
 *
 * <p>
 * The density of a solution is computed as the distance to its nearest neighbor
 * after shifting. Lower density (smaller distance to shifted neighbors) means
 * crowded
 * and/or dominated region.
 *
 * @author Antonio J. Nebro
 * @param <S> Type of solution
 */
public class ShiftedDensityEstimator<S extends Solution<?>> implements DensityEstimator<S> {

  private static final String ATTRIBUTE_ID = ShiftedDensityEstimator.class.getName();
  private final int k; // k-th nearest neighbor for density estimation

  /**
   * Constructor using k=1 (nearest neighbor).
   */
  public ShiftedDensityEstimator() {
    this(1);
  }

  /**
   * Constructor with configurable k for k-th nearest neighbor.
   *
   * @param k The k-th nearest neighbor to use (1 = nearest, 2 = second nearest,
   *          etc.)
   */
  public ShiftedDensityEstimator(int k) {
    Check.that(k >= 1, "k must be >= 1");
    this.k = k;
  }

  @Override
  public void compute(List<S> solutionList) {
    if (solutionList.isEmpty()) {
      return;
    }

    if (solutionList.size() == 1) {
      solutionList.get(0).attributes().put(ATTRIBUTE_ID, Double.POSITIVE_INFINITY);
      return;
    }

    int n = solutionList.size();
    int m = solutionList.get(0).objectives().length;

    // Compute SDE distance for each solution
    for (int i = 0; i < n; i++) {
      S solutionI = solutionList.get(i);
      double[] objectivesI = solutionI.objectives();

      // Find k-th nearest neighbor using shifted distance
      double[] distances = new double[n];
      for (int j = 0; j < n; j++) {
        if (i == j) {
          distances[j] = Double.POSITIVE_INFINITY;
          continue;
        }

        S solutionJ = solutionList.get(j);
        distances[j] = shiftedDistance(objectivesI, solutionJ.objectives(), m);
      }

      // Find k-th smallest distance (k-th nearest neighbor)
      double kthDistance = findKthSmallest(distances, k);
      solutionI.attributes().put(ATTRIBUTE_ID, kthDistance);
    }
  }

  /**
   * Computes the shifted Euclidean distance from solution p to shifted solution
   * q.
   *
   * <p>
   * For each objective j:
   * - If q[j] >= p[j] (q is worse or equal), shift q[j] to p[j] (distance
   * component = 0)
   * - If q[j] < p[j] (q is better), use actual difference
   *
   * <p>
   * This shifting operation penalizes solutions in dominated or partially
   * dominated regions.
   *
   * @param p Objectives of solution p (the solution being evaluated)
   * @param q Objectives of solution q (the neighbor)
   * @param m Number of objectives
   * @return Shifted Euclidean distance
   */
  private double shiftedDistance(double[] p, double[] q, int m) {
    double sum = 0.0;

    for (int j = 0; j < m; j++) {
      double shiftedQ;
      if (q[j] >= p[j]) {
        // q is worse or equal in objective j, shift to p's position
        shiftedQ = p[j];
      } else {
        // q is better in objective j, keep original position
        shiftedQ = q[j];
      }

      double diff = p[j] - shiftedQ;
      sum += diff * diff;
    }

    return Math.sqrt(sum);
  }

  /**
   * Finds the k-th smallest value in an array.
   */
  private double findKthSmallest(double[] distances, int k) {
    // Simple approach: sort a copy and return k-th element
    double[] sorted = distances.clone();
    java.util.Arrays.sort(sorted);

    // k is 1-indexed, so k=1 means the smallest (index 0)
    int index = Math.min(k - 1, sorted.length - 1);
    return sorted[index];
  }

  @Override
  public Double value(S solution) {
    Check.notNull(solution);
    Double result = (Double) solution.attributes().get(ATTRIBUTE_ID);
    return result != null ? result : 0.0;
  }

  @Override
  public Comparator<S> comparator() {
    // Higher distance = less crowded = better (preferred for survival)
    return Comparator.comparing(this::value).reversed();
  }
}
