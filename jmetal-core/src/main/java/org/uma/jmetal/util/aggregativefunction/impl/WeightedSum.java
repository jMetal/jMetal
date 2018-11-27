package org.uma.jmetal.util.aggregativefunction.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;

public class WeightedSum<S extends Solution<?>> implements AggregativeFunction<S> {

  @Override
  public double compute(S solution, double[] weightVector) {
    double sum = 0.0;
    for (int n = 0; n < solution.getNumberOfObjectives(); n++) {
      sum += (weightVector[n]) * solution.getObjective(n);
    }

    return sum;
  }
}
