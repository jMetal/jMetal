package org.uma.jmetal.util.point.util.comparator;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.point.Point;

import java.util.Comparator;

/**
 * This class implements the Comparator interface for comparing tow points.
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
  public int compare(Point pointOne, Point pointTwo) {
    if (pointOne ==  null) {
      throw new JMetalException("PointOne is null") ;
    } else if (pointTwo == null) {
      throw new JMetalException("PointTwo is null");
    }

    // Determine the first i such as pointOne[i] != pointTwo[i];
    int index = 0;
    while ((index < pointOne.getNumberOfDimensions())
        && (index < pointTwo.getNumberOfDimensions())
        && pointOne.getDimensionValue(index) == pointTwo.getDimensionValue(index)) {
      index++;
    }

    int result = 0 ;
    if ((index >= pointOne.getNumberOfDimensions()) || (index >= pointTwo.getNumberOfDimensions())) {
      result = 0;
    } else if (pointOne.getDimensionValue(index) < pointTwo.getDimensionValue(index)) {
      result = -1;
    } else if (pointOne.getDimensionValue(index) > pointTwo.getDimensionValue(index)) {
      result = 1;
    }
    return result ;
  }
}
