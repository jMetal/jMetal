package org.uma.jmetal.util.aggregationfunction;

public interface AggregationFunction {
  double compute(double[] vector, double[] weightVector) ;
  void update(double[] vector) ;
  void reset() ;
}
