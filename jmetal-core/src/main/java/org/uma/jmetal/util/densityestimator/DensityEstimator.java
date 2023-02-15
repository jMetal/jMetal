package org.uma.jmetal.util.densityestimator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * Interface representing implementations to compute the crowding distance
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DensityEstimator<S> extends Serializable {

  void compute(List<S> solutionSet);

  Double value(S solution);

  Comparator<S> comparator();
}
