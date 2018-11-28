package org.uma.jmetal.util.point.util.distance;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.Point;

/**
 * Computes the distance between two points a y b according to the dominance relationship. Point a
 * is supposed to be point of the Pareto front
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DominanceDistance implements PointDistance {

  @Override
  public double compute(Point a, Point b) {
    if (a == null) {
      throw new JMetalException("The first point is null") ;
    } else if (b == null) {
      throw new JMetalException("The second point is null") ;
    } else if (a.getDimension() != b.getDimension()) {
      throw new JMetalException("The dimensions of the points are different: "
          + a.getDimension() + ", " + b.getDimension()) ;
    }

    double distance = 0.0;

    for (int i = 0; i < a.getDimension(); i++) {
      double max = Math.max(b.getValue(i) - a.getValue(i), 0.0) ;
      distance += Math.pow(max, 2);
    }
    return Math.sqrt(distance);
  }
}
