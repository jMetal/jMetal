package org.uma.jmetal.util.aggregativefunction.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.point.impl.IdealPoint;

public class Tschebyscheff<S extends Solution<?>> implements AggregativeFunction<S> {
  private IdealPoint idealPoint ;

  public Tschebyscheff(IdealPoint idealPoint) {
    this.idealPoint = idealPoint ;
  }

  @Override
  public double compute(S solution, double[] weightVector) {
    double maxFun = -1.0e+30;

    for (int n = 0; n < solution.getNumberOfObjectives(); n++) {
      double diff = Math.abs(solution.getObjective(n) - idealPoint.getValue(n));

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
}
