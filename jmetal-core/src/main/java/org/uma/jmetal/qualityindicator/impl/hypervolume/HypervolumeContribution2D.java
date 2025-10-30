package org.uma.jmetal.qualityindicator.impl.hypervolume;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Efficient O(n log n) algorithm for computing exclusive hypervolume contributions
 * for each point in a set of solutions for 2-objective problems.
 * 
 * <p>Based on the dimension-sweep algorithm by Fonseca et al. [1] as implemented in moocore.
 * 
 * <p><b>References:</b>
 * <ul>
 * <li>[1] C. M. Fonseca, L. Paquete, and M. Lopez-Ibanez. An improved dimension-sweep 
 * algorithm for the hypervolume indicator. In IEEE Congress on Evolutionary Computation, 
 * pages 1157-1163, Vancouver, Canada, July 2006.</li>
 * </ul>
 * 
 * @author Antonio J. Nebro
 */
public class HypervolumeContribution2D {

  /**
   * Auxiliary class to maintain a point with its original index.
   */
  private static class IndexedPoint {
    final double[] objectives;
    final int originalIndex;
    
    IndexedPoint(double[] objectives, int originalIndex) {
      this.objectives = objectives;
      this.originalIndex = originalIndex;
    }
  }

  /**
   * Computes the exclusive hypervolume contributions for each point.
   * 
   * <p>The exclusive contribution of a point is the hypervolume that is lost
   * when that point is removed from the set. Dominated points contribute 0.
   * Duplicate points contribute 0.
   * 
   * @param points Matrix of points (n × 2), where each row is a point [obj1, obj2]
   * @param ref Reference point [ref1, ref2]. Must weakly dominate all points.
   * @return Array of n contributions. Points that don't strictly dominate ref have contribution 0.
   */
  public static double[] compute(double[][] points, double[] ref) {
    if (points == null || points.length == 0) {
      return new double[0];
    }
    
    if (ref == null || ref.length != 2) {
      throw new IllegalArgumentException("Reference point must have 2 objectives");
    }
    
    int n = points.length;
    double[] contributions = new double[n];
    
    // Special case: single point
    if (n == 1) {
      if (stronglyDominates(points[0], ref)) {
        contributions[0] = (ref[0] - points[0][0]) * (ref[1] - points[0][1]);
      }
      return contributions;
    }
    
    // Create array of indexed points to track original positions
    IndexedPoint[] indexed = new IndexedPoint[n];
    for (int i = 0; i < n; i++) {
      indexed[i] = new IndexedPoint(points[i], i);
    }
    
    // Sort by first dimension (ascending), tie-break by second (descending)
    Arrays.sort(indexed, Comparator
        .comparingDouble((IndexedPoint p) -> p.objectives[0])
        .thenComparingDouble((IndexedPoint p) -> -p.objectives[1]));
    
    // Filter points that don't strictly dominate the reference point
    // and find the first valid point
    int firstValid = -1;
    for (int i = 0; i < n; i++) {
      if (stronglyDominates(indexed[i].objectives, ref)) {
        firstValid = i;
        break;
      }
    }
    
    if (firstValid == -1) {
      // No point dominates the reference point
      return contributions;
    }
    
    // Skip points above ref in second objective
    int j = firstValid;
    while (j < n && indexed[j].objectives[1] >= ref[1]) {
      j++;
    }
    
    if (j == n) {
      // All points are above ref[1]
      return contributions;
    }
    
    // Dimension-sweep algorithm based on moocore hvc2d_nondom
    double height = ref[1] - indexed[j].objectives[1];
    IndexedPoint prev = indexed[j];
    j++;
    
    while (j < n) {
      IndexedPoint curr = indexed[j];
      
      // Check if curr strictly dominates ref
      if (!stronglyDominates(curr.objectives, ref)) {
        j++;
        continue;
      }
      
      if (prev.objectives[1] > curr.objectives[1]) {
        // curr improves in the second dimension compared to prev
        // Calculate contribution of prev
        double width = curr.objectives[0] - prev.objectives[0];
        contributions[prev.originalIndex] = width * height;
        
        // Update height for the next segment
        height = prev.objectives[1] - curr.objectives[1];
        prev = curr;
        j++;
        
      } else if (prev.objectives[0] == curr.objectives[0]) {
        // Same value in first dimension
        if (prev.objectives[1] == curr.objectives[1]) {
          // Exact duplicate - contributes 0
          height = 0;
          prev = curr;
        }
        // Skip all points with same x (they are weakly dominated by prev)
        while (j < n && indexed[j].objectives[0] == prev.objectives[0]) {
          j++;
        }
        
      } else {
        // prev.objectives[0] < curr.objectives[0] && prev.objectives[1] <= curr.objectives[1]
        // curr is dominated by prev
        // Skip all points dominated by prev
        while (j < n && prev.objectives[1] <= indexed[j].objectives[1]) {
          j++;
        }
      }
    }
    
    // Contribution of the last valid point
    if (prev != null && stronglyDominates(prev.objectives, ref)) {
      contributions[prev.originalIndex] = (ref[0] - prev.objectives[0]) * height;
    }
    
    return contributions;
  }
  
  /**
   * Checks if point p strictly dominates point q.
   * p strictly dominates q if p[i] < q[i] for all i (minimization).
   */
  private static boolean stronglyDominates(double[] p, double[] q) {
    return p[0] < q[0] && p[1] < q[1];
  }
  
  /**
   * Computes the total hypervolume of the set of points.
   * 
   * @param points Matrix of points (n × 2)
   * @param ref Reference point [ref1, ref2]
   * @return Total hypervolume
   */
  public static double computeHypervolume(double[][] points, double[] ref) {
    double[] contributions = compute(points, ref);
    double total = 0.0;
    for (double c : contributions) {
      total += c;
    }
    return total;
  }
}
