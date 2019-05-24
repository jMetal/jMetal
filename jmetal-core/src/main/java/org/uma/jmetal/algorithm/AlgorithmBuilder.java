package org.uma.jmetal.algorithm;

import org.uma.jmetal.algorithm.Algorithm;

/**
 * Interface representing algorithm builders
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface AlgorithmBuilder<A extends Algorithm<?>> {
  A build() ;
}
