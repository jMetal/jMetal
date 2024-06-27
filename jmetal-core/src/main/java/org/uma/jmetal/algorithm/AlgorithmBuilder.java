package org.uma.jmetal.algorithm;

/**
 * Interface representing algorithm builders
 *
 * @author Antonio J. Nebro
 */
public interface AlgorithmBuilder<A extends Algorithm<?>> {
  A build() ;
}
