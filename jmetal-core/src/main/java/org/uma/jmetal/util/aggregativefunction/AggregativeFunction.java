package org.uma.jmetal.util.aggregativefunction;

import org.uma.jmetal.solution.Solution;

public interface AggregativeFunction {
  double compute(double[] vector, double[] weightVector) ;
  void update(double[] vector) ;
}
