package org.uma.jmetal.util.aggregationfunction.impl;

import org.uma.jmetal.util.aggregationfunction.AggregationFunction;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

public class Tschebyscheff implements AggregationFunction {
  private IdealPoint idealPoint ;
  private NadirPoint nadirPoint ;

  public Tschebyscheff() {
    this.idealPoint = null ;
  }

  @Override
  public double compute(double[] vector, double[] weightVector) {
    double maxFun = -1.0e+30;

    for (int n = 0; n < vector.length; n++) {
      double diff = Math.abs((vector[n] - idealPoint.getValue(n))/(nadirPoint.getValue(n)-idealPoint.getValue(n)));

      double feval;
      if (weightVector[n] == 0) {
        feval = 0.0001 * diff;
      } else {
        feval = diff * weightVector[n];
      }
      if (feval > maxFun) {
        maxFun = feval;
      }
    }

    return maxFun;
  }


  @Override
  public void update(double[] vector) {
    if (idealPoint == null) {
      idealPoint = new IdealPoint(vector.length) ;
      nadirPoint = new NadirPoint(vector.length) ;
    }
    idealPoint.update(vector);
    nadirPoint.update(vector);
  }

  @Override
  public void reset() {
    idealPoint = null ;
    nadirPoint = null ;
  }
}
