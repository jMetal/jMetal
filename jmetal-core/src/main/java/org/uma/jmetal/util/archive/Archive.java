package org.uma.jmetal.util.archive;

import java.io.Serializable;
import java.util.List;

/**
 * Interface representing an archive of solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Archive<S> extends Serializable{
  public boolean add(S solution) ;
  public S get(int index) ;
  public List<S> getSolutionList() ;
  public int size() ;
}
