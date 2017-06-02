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
    } else if (a.getNumberOfDimensions() != b.getNumberOfDimensions()) {
      throw new JMetalException("The dimensions of the points are different: "
          + a.getNumberOfDimensions() + ", " + b.getNumberOfDimensions()) ;
    }

    double distance = 0.0;

    for (int i = 0; i < a.getNumberOfDimensions(); i++) {
      double max = Math.max(b.getDimensionValue(i) - a.getDimensionValue(i), 0.0) ;
      distance += Math.pow(max, 2);
    }
    return Math.sqrt(distance);
  }
}
