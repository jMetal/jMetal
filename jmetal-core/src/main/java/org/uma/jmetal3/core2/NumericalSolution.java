package org.uma.jmetal3.core2;

import org.uma.jmetal3.core.Solution;

/**
 * Created by antonio on 03/09/14.
 */
public interface NumericalSolution<V extends Number> extends Solution {
  public V getUpperBound(int index) ;
 }
