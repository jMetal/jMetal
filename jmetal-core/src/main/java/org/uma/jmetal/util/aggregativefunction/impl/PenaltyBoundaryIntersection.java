package org.uma.jmetal.util.aggregativefunction.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.point.impl.IdealPoint;

public class PenaltyBoundaryIntersection<S extends Solution<?>> implements AggregativeFunction<S> {
  private IdealPoint idealPoint ;

  public PenaltyBoundaryIntersection(IdealPoint idealPoint) {
    this.idealPoint = idealPoint ;
  }

  @Override
  public double compute(S solution, double[] weightVector) {
    double d1, d2, nl;
    double theta = 5.0;

    d1 = d2 = nl = 0.0;

    for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
      d1 += (solution.getObjective(i) - idealPoint.getValue(i)) * weightVector[i];
      nl += Math.pow(weightVector[i], 2.0);
    }
    nl = Math.sqrt(nl);
    d1 = Math.abs(d1) / nl;

    for (int i = 0; i < solution.getNumberOfObjectives(); i++) {
      d2 += Math.pow((solution.getObjective(i) - idealPoint.getValue(i)) -
          d1 * (weightVector[i] / nl), 2.0);
    }
    d2 = Math.sqrt(d2);

    return (d1 + theta * d2) ;
  }
}
