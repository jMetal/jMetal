package org.uma.jmetal.util.point.impl;

import org.uma.jmetal.util.point.Point;

/**
 * Created by ajnebro on 5/1/15.
 */
public class PointUtils {
  /**
   * This method returns the distance (taken the euclidean distance) between
   * two points
   *
   * @param a A point
   * @param b A point
   * @return The euclidean distance between the points
   */
  public static double euclideanDistance(Point a, Point b) {
    double distance = 0.0;

    for (int i = 0; i < a.getNumberOfDimensions(); i++) {
      distance += Math.pow(a.getDimensionValue(i) - b.getDimensionValue(i), 2.0);
    }
    return Math.sqrt(distance);
  }
}
