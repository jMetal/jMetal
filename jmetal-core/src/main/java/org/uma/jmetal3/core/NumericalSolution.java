package org.uma.jmetal3.core;

/**
 * Created by antonio on 03/09/14.
 */
public interface NumericalSolution<V> extends Solution {
  public V getUpperBound(int index) ;
 }
