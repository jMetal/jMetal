package org.uma.jmetal.util.archive;

import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by Antonio J. Nebro on 24/09/14.
 */
public interface Archive<T extends Solution> {
  public boolean add(T solution) ;
  public List<T> getSolutionList() ;
  public int getMaxSize() ;

}
