package org.uma.jmetal3.util.solutionattribute;

import org.uma.jmetal3.core.Solution;

/**
 * Created by Antonio J. Nebro on 03/10/14.
 */
public interface SolutionAttribute <S extends Solution<?>, V> {
  public void setAttribute(S solution, V value) ;
  public V getAttribute(S solution) ;
  public Object getAttributeID() ;
}
