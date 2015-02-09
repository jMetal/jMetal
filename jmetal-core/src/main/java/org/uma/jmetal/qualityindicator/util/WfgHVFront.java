package org.uma.jmetal.qualityindicator.util;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.point.Point;

import java.util.List;

/**
 * Created by ajnebro on 3/2/15.
 */
public class WfgHvFront extends ArrayFront {

  public WfgHvFront() {
    super();
  }

  public WfgHvFront(List<? extends Solution> solutionList) {
    super(solutionList) ;
  }

  public WfgHvFront(int numberOfPoints, int dimensions) {
    super(numberOfPoints, dimensions) ;
  }

  public void setNumberOfPoints(int numberOfPoints) {
    this.numberOfPoints = numberOfPoints ;
  }

  @Override public int getNumberOfPoints() {
    return numberOfPoints ;
  }

  @Override public Point getPoint(int index) {
    if (index < 0) {
      throw new JMetalException("The index value is negative") ;
    }

    return points[index];
  }

  @Override public void setPoint(int index, Point point) {
    if (index < 0) {
      throw new JMetalException("The index value is negative") ;
    } else if (point == null) {
      throw new JMetalException("The point is null") ;
    }
    points[index] = point ;
  }
}
