package org.uma.jmetal.util.point.util;

import org.uma.jmetal.util.point.Point;

/**
 * Computes a distance between two points
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface PointDistance {
  public double compute(Point pointA, Point pointB) ;
}
