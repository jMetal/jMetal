package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.point.Point;

/**
 * Created by ajnebro on 2/2/15.
 */
public class FastHypervolume {
  private Point referencePoint;
  private int numberOfObjectives;
  private double offset = 20.0;

  public FastHypervolume() {
    referencePoint = null;
    numberOfObjectives = 0;
  }

  public FastHypervolume(double offset) {
    referencePoint = null;
    numberOfObjectives = 0;
    this.offset = offset;
  }

  /**
   * Updates the reference point
   */
  private void updateReferencePoint(Front front) {
    double[] maxObjectives = new double[numberOfObjectives];
    for (int i = 0; i < numberOfObjectives; i++) {
      maxObjectives[i] = 0;
    }

    for (int i = 0; i < front.getNumberOfPoints(); i++) {
      for (int j = 0; j < numberOfObjectives; j++) {
        if (maxObjectives[j] < front.getPoint(i).getDimensionValue(j)) {
          maxObjectives[j] = front.getPoint(i).getDimensionValue(j) ;
        }
      }
    }

    for (int i = 0; i < referencePoint.getNumberOfDimensions(); i++) {
      referencePoint.setDimensionValue(i, maxObjectives[i] + offset);
    }
  }
}
