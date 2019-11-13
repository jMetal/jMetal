package org.uma.jmetal.util.neighborhood;

import java.io.Serializable;
import java.util.List;

/**
 * Interface representing a neighborhood of a given solution in a list of solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface Neighborhood<S> extends Serializable {
  public List<S> getNeighbors(List<S> solutionList, int solutionIndex) ;
}
