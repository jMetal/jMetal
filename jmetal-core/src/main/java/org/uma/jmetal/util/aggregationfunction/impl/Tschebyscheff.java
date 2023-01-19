package org.uma.jmetal.util.aggregationfunction.impl;

import org.uma.jmetal.util.aggregationfunction.AggregationFunction;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

public class Tschebyscheff implements AggregationFunction {

  private boolean normalizeObjectives ;

  public Tschebyscheff(boolean normalizeObjectives) {
    this.normalizeObjectives = normalizeObjectives ;
  }

  @Override
  public double compute(double[] vector, double[] weightVector, IdealPoint idealPoint, NadirPoint nadirPoint) {
    double maxFun = -1.0e+30;

    for (int n = 0; n < vector.length; n++) {
      double diff ;
      if (normalizeObjectives) {
        diff = Math.abs((vector[n] - idealPoint.getValue(n))/(nadirPoint.getValue(n)-idealPoint.getValue(n)));
      } else {
        diff = Math.abs(vector[n] - idealPoint.getValue(n));
      }

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
  public boolean normalizeObjectives() {
    return this.normalizeObjectives ;
  }
}
