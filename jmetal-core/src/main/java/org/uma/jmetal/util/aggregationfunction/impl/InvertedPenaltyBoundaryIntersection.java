package org.uma.jmetal.util.aggregationfunction.impl;

import org.uma.jmetal.util.aggregationfunction.AggregationFunction;
import org.uma.jmetal.util.point.impl.NadirPoint;

public class InvertedPenaltyBoundaryIntersection implements AggregationFunction {
  private NadirPoint nadirPoint ;
  private final double theta ;

  public InvertedPenaltyBoundaryIntersection() {
    this(5.0) ;
  }

  public InvertedPenaltyBoundaryIntersection(double theta) {
    this.nadirPoint = null ;
    this.theta = theta ;
  }

  @Override
  public double compute(double[] vector, double[] weightVector) {
    double d1, d2, nl;

    d1 = d2 = nl = 0.0;

    for (int i = 0; i < vector.length; i++) {
      d1 += (nadirPoint.value(i)- vector[i]) * weightVector[i];
      nl += Math.pow(weightVector[i], 2.0);
    }
    nl = Math.sqrt(nl);
    d1 = Math.abs(d1) / nl;

    for (int i = 0; i < vector.length; i++) {
      d2 += Math.pow((nadirPoint.value(i) - vector[i]) -
          d1 * (weightVector[i] / nl), 2.0);
    }
    d2 = Math.sqrt(d2);

    return (d1 - theta * d2) ;
  }

  @Override
  public void update(double[] vector) {
    if (nadirPoint == null) {
      nadirPoint = new NadirPoint(vector.length) ;
    }
    nadirPoint.update(vector);
  }
}
