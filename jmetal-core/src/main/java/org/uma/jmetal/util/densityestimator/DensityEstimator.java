package org.uma.jmetal.util.densityestimator;

import java.util.Comparator;
import java.util.List;

/**
 * Interface representing implementations to compute the crowding distance
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DensityEstimator<S> {

  void compute(List<S> solutionSet);

  Double getValue(S solution);

  Comparator<S> getComparator();
}
