package org.uma.jmetal.util.solutionattribute;

import java.util.List;

/**
 * Interface representing implementations to compute the crowding distance
 *
 * @author Antonio J. Nebro
 */
@Deprecated
public interface DensityEstimator<S> extends SolutionAttribute<S, Double>{
  void computeDensityEstimator(List<S> solutionSet) ;
}

