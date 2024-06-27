package org.uma.jmetal.util.solutionattribute;

import java.io.Serializable;

/**
 * Attributes allows to extend the solution classes to incorporate data required by
 * operators or algorithms manipulating them.
 *
 * @author Antonio J. Nebro
 */
@Deprecated
public interface SolutionAttribute <S, V> extends Serializable {
  void setAttribute(S solution, V value) ;
  V getAttribute(S solution) ;
  Object getAttributeIdentifier() ;
}
