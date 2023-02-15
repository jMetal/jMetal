package org.uma.jmetal.util.aggregationfunction.impl;

import org.uma.jmetal.util.aggregationfunction.AggregationFunction;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

public class ModifiedTschebyscheff implements AggregationFunction {
  private boolean normalizeObjectives ;

  public ModifiedTschebyscheff(boolean normalizeObjectives) {
    this.normalizeObjectives = normalizeObjectives ;
  }

  @Override
  public double compute(double[] vector, double[] weightVector, IdealPoint idealPoint, NadirPoint nadirPoint) {
    double maxFun = -1.0e+30;

    for (int n = 0; n < vector.length; n++) {
      double diff ;
      if (normalizeObjectives) {
        diff = Math.abs((vector[n] - idealPoint.value(n))/(nadirPoint.value(n)-idealPoint.value(n)));
      } else {
        diff = Math.abs(vector[n] - idealPoint.value(n));
      }

      double feval;
      if (weightVector[n] == 0) {
        feval = 0.0000001 * diff;
      } else {
        feval = diff * 1.0/weightVector[n];
      }
      if (feval > maxFun) {
        maxFun = feval;
      }
    }

    return maxFun;
  }

  @Override
  public boolean normalizeObjectives() {
    return this.normalizeObjectives;
  }
}
