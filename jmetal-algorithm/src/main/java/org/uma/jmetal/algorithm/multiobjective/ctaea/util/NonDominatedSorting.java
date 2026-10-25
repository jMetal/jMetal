package org.uma.jmetal.algorithm.multiobjective.ctaea.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Non-dominated sorting of a matrix of values to minimize, as the {@code NDSort(F, inf)} function
 * of PlatEMO: it uses Pareto dominance only, without constraints, and equal vectors are in the
 * same front.
 */
public final class NonDominatedSorting {
  private NonDominatedSorting() {}

  /**
   * Returns whether a vector Pareto-dominates another one (minimization): it is not worse in any
   * component and it is better in at least one.
   *
   * @param a a vector
   * @param b a vector of the same length
   * @return true if {@code a} dominates {@code b}
   */
  public static boolean dominates(double[] a, double[] b) {
    boolean better = false;
    for (int k = 0; k < a.length; k++) {
      if (a[k] > b[k]) {
        return false;
      }
      if (a[k] < b[k]) {
        better = true;
      }
    }
    return better;
  }

  /**
   * Computes the front number of every vector: 1 for the non-dominated vectors, 2 for the
   * vectors that are non-dominated once those of the first front are removed, and so on.
   *
   * @param points the vectors (rows) to sort
   * @return the front number of each row, starting at 1
   */
  public static int[] frontNumbers(double[][] points) {
    int n = points.length;
    int[] dominationCount = new int[n];
    List<List<Integer>> dominated = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      dominated.add(new ArrayList<>());
    }
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        if (dominates(points[i], points[j])) {
          dominated.get(i).add(j);
          dominationCount[j]++;
        } else if (dominates(points[j], points[i])) {
          dominated.get(j).add(i);
          dominationCount[i]++;
        }
      }
    }

    int[] front = new int[n];
    List<Integer> current = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      if (dominationCount[i] == 0) {
        current.add(i);
      }
    }
    int frontNumber = 1;
    while (!current.isEmpty()) {
      List<Integer> next = new ArrayList<>();
      for (int i : current) {
        front[i] = frontNumber;
        for (int j : dominated.get(i)) {
          dominationCount[j]--;
          if (dominationCount[j] == 0) {
            next.add(j);
          }
        }
      }
      current = next;
      frontNumber++;
    }
    return front;
  }
}
