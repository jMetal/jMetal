package org.uma.jmetal.util.archive;

import java.util.Comparator;

/**
 * Interface representing a bounded archive of solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface BoundedArchive<S> extends Archive<S> {
  int getMaxSize() ;
  Comparator<S> getComparator() ;
  void computeDensityEstimator() ;
  void sortByDensityEstimator() ;
}
