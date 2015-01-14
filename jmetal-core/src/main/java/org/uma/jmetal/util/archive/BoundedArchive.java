package org.uma.jmetal.util.archive;

import org.uma.jmetal.solution.Solution;

/**
 * Created by Antonio J. Nebro on 24/09/14.
 */
public interface BoundedArchive<S extends Solution> extends Archive<S> {
  public int getMaxSize() ;
}
