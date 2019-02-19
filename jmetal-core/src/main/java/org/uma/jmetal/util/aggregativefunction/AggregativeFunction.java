package org.uma.jmetal.util.aggregativefunction;

public interface AggregativeFunction {
  double compute(double[] vector, double[] weightVector) ;
  void update(double[] vector) ;
}
