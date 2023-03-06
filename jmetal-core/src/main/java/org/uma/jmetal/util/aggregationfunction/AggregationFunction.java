package org.uma.jmetal.util.aggregationfunction;

import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

public interface AggregationFunction {
  double compute(double[] vector, double[] weightVector, IdealPoint idealPoint, NadirPoint nadirPoint) ;
  boolean normalizeObjectives() ;
  void epsilon(double value) ;
}
