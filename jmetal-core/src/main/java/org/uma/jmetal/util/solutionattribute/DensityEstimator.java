package org.uma.jmetal.util.solutionattribute;

import java.util.List;

/**
 * Interface representing implementations to compute the crowding distance
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DensityEstimator<S> extends SolutionAttribute<S, Double>{
  public void computeDensityEstimator(List<S> solutionSet) ;
}

