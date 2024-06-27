package org.uma.jmetal.util.archive;

import java.io.Serializable;
import java.util.List;

/**
 * Interface representing an archive of solutions
 *
 * @author Antonio J. Nebro
 */
public interface Archive<S> extends Serializable {
  boolean add(S solution) ;
  S get(int index) ;
  List<S> solutions() ;
  int size() ;
}
