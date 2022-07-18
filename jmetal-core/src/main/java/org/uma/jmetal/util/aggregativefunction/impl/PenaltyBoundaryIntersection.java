package org.uma.jmetal.util.aggregativefunction.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.point.impl.IdealPoint;

public class PenaltyBoundaryIntersection implements AggregativeFunction {
  private @Nullable IdealPoint idealPoint ;
  private final double theta ;

  public PenaltyBoundaryIntersection() {
    this(5.0) ;
  }

  public PenaltyBoundaryIntersection(double theta) {
    this.idealPoint = null ;
    this.theta = theta ;
  }

  @Override
  public double compute(double @NotNull [] vector, double[] weightVector) {
    double d1, d2, nl;

    d1 = d2 = nl = 0.0;

    for (var i = 0; i < vector.length; i++) {
      d1 += (vector[i] - idealPoint.getValue(i)) * weightVector[i];
      nl += Math.pow(weightVector[i], 2.0);
    }
    nl = Math.sqrt(nl);
    d1 = Math.abs(d1) / nl;

    for (var i = 0; i < vector.length; i++) {
      d2 += Math.pow((vector[i] - idealPoint.getValue(i)) -
          d1 * (weightVector[i] / nl), 2.0);
    }
    d2 = Math.sqrt(d2);

    return (d1 + theta * d2) ;
  }

  @Override
  public void update(double[] vector) {
    if (idealPoint == null) {
      idealPoint = new IdealPoint(vector.length) ;
    }
    idealPoint.update(vector);
  }
}
