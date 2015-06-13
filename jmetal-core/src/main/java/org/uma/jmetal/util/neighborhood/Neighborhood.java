package org.uma.jmetal.util.neighborhood;

import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by ajnebro on 20/5/15.
 */
public interface Neighborhood<S extends Solution<?>> {
  public List<S> getNeighbors(List<S> solutionList, int solutionIndex) ;
}
