package org.uma.jmetal.util.front;

import org.uma.jmetal.util.point.Point;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A front is a list of points
 *
 * @author Antonio J. Nebro
 */
public interface Front extends Serializable {
  public int getNumberOfPoints() ;
  public int getPointDimensions() ;
  public Point getPoint(int index) ;
  public void setPoint(int index, Point point) ;
  public void sort(Comparator<Point> comparator) ;
}
