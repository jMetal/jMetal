package org.uma.jmetal.util.archive;

import org.uma.jmetal.solution.Solution;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 24/09/14.
 */
public interface Archive<S extends Solution> extends Serializable {
  public boolean add(S solution) ;
  public List<S> getSolutionList() ;
}
