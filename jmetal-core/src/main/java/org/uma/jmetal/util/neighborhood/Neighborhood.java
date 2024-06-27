package org.uma.jmetal.util.neighborhood;

import java.io.Serializable;
import java.util.List;

/**
 * Interface representing a neighborhood of a given solution in a list of solutions
 *
 * @author Antonio J. Nebro
 */
public interface Neighborhood<S> extends Serializable {
  enum NeighborType {
    NEIGHBOR,
    POPULATION,
    ARCHIVE
  }
  List<S> getNeighbors(List<S> solutionList, int solutionIndex) ;
}
