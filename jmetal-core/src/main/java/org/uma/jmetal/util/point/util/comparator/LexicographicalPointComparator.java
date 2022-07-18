package org.uma.jmetal.util.point.util.comparator;

import java.util.Comparator;

import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.point.Point;

/**
 * This class implements the Comparator interface for comparing two points.
 * The order used is lexicographical order.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo

 */
public class LexicographicalPointComparator implements Comparator<Point> {

  /**
   * The compare method compare the objects o1 and o2.
   *
   * @param pointOne An object that reference a double[]
   * @param pointTwo An object that reference a double[]
   * @return The following value: -1 if point1 < point2, 1 if point1 > point2 or 0 in other case.
   */
  @Override
  public int compare(@Nullable Point pointOne, Point pointTwo) {
    if (pointOne ==  null) {
      throw new JMetalException("PointOne is null") ;
    } else if (pointTwo == null) {
      throw new JMetalException("PointTwo is null");
    }

    // Determine the first i such as pointOne[i] != pointTwo[i];
    int index = 0;
    while ((index < pointOne.getDimension())
        && (index < pointTwo.getDimension())
        && pointOne.getValue(index) == pointTwo.getValue(index)) {
      index++;
    }

    int result = 0 ;
    if ((index >= pointOne.getDimension()) || (index >= pointTwo.getDimension())) {
      result = 0;
    } else if (pointOne.getValue(index) < pointTwo.getValue(index)) {
      result = -1;
    } else if (pointOne.getValue(index) > pointTwo.getValue(index)) {
      result = 1;
    }
    return result ;
  }
}
