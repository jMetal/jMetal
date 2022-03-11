package org.uma.jmetal.util.legacy.front;

import java.io.Serializable;
import java.util.Comparator;
import org.uma.jmetal.util.point.Point;

/**
 * A front is a list of points
 *
 * @author Antonio J. Nebro
 */
@Deprecated
public interface Front extends Serializable {
  int getNumberOfPoints();

  int getPointDimensions();

  Point getPoint(int index);

  void setPoint(int index, Point point);

  void sort(Comparator<Point> comparator);

  double[][] getMatrix() ;
}
