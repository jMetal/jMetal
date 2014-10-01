package org.uma.jmetal3.util.archive;

import org.uma.jmetal3.core.Solution;

import java.util.List;

/**
 * Created by Antonio J. Nebro on 24/09/14.
 */
public interface Archive {
  public boolean add(Solution<?> solution) ;
  public List<Solution<?>> getSolutionList() ;
  public int getMaxSize() ;

}
