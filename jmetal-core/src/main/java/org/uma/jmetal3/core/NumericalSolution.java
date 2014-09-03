package org.uma.jmetal3.core;

import org.uma.jmetal3.core.Solution;

/**
 * Created by Antonio on 03/09/14.
 */
public interface NumericalSolution<V extends Number> extends Solution {
  public V getLowerBound(int index) ;
  public V getUpperBound(int index) ;
 }
