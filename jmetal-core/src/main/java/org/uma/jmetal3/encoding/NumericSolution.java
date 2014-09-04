package org.uma.jmetal3.encoding;

import org.uma.jmetal3.core.Solution;

/**
 * Created by Antonio on 03/09/14.
 */
public interface NumericSolution<V extends Number> extends Solution {
  public V getLowerBound(int index) ;
  public V getUpperBound(int index) ;
 }
