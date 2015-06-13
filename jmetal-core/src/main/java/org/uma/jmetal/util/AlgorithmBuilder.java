package org.uma.jmetal.util;

import org.uma.jmetal.algorithm.Algorithm;

/**
 * Created by ajnebro on 3/1/15.
 */
public interface AlgorithmBuilder<A extends Algorithm<?>> {
  public A build() ;
}
