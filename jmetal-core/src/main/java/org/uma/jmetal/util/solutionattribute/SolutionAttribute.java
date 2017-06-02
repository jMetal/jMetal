package org.uma.jmetal.util.solutionattribute;

import java.io.Serializable;

/**
 * Attributes allows to extend the solution classes to incorporate data required by
 * operators or algorithms manipulating them.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface SolutionAttribute <S, V> extends Serializable {
  public void setAttribute(S solution, V value) ;
  public V getAttribute(S solution) ;
  public Object getAttributeIdentifier() ;
}
