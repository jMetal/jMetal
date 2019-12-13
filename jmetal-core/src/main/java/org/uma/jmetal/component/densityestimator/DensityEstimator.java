package org.uma.jmetal.component.densityestimator;

import org.uma.jmetal.solution.util.attribute.Attribute;

import java.util.List;

/**
 * Interface representing implementations to compute the crowding distance
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DensityEstimator<S> extends Attribute<S> {
  void computeDensityEstimator(List<S> solutionSet) ;

  List<S> sort(List<S> solutionList) ;
}
