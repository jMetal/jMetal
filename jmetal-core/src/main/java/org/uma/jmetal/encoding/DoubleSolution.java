package org.uma.jmetal.encoding;

import org.uma.jmetal.core.Solution;

/**
 * Created by Antonio on 03/09/14.
 */
public interface DoubleSolution extends Solution<Double> {
  public Double getLowerBound(int index) ;
  public Double getUpperBound(int index) ;
 }
