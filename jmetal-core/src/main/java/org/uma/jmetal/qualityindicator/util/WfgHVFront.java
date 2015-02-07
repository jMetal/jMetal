package org.uma.jmetal.qualityindicator.util;

import org.uma.jmetal.util.front.imp.ArrayFront;

/**
 * Created by ajnebro on 3/2/15.
 */
public class WfgHVFront extends ArrayFront {

  public WfgHVFront() {
    super();
  }

  public WfgHVFront(int numberOfPoints, int dimensions) {
    super(numberOfPoints, dimensions) ;
  }

  public void setNumberOfPoints(int numberOfPoints) {
    this.numberOfPoints = numberOfPoints ;
  }

  @Override public int getNumberOfPoints() {
    return numberOfPoints ;
  }
}
