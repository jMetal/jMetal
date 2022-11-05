package org.uma.jmetal.util.neighborhood.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.neighborhood.Neighborhood;

/**
 * This class implements a ring-based neighborhood. The neighbours of the solution at position i are
 * the particles a positions i - 1 and i + 1.
 *
 * @param <S>
 */
public class RingNeighborhood<S extends Solution<?>> implements Neighborhood<S> {

  @Override
  public List<S> getNeighbors(List<S> solutionList, int solutionIndex) {
    Check.notNull(solutionList);
    Check.collectionIsNotEmpty(solutionList);
    Check.that(solutionList.size() > 1,
        "The solution list size " + solutionList.size() + " is not higher than one");
    Check.that(solutionIndex < solutionList.size(),
        "The index " + solutionIndex + " is equal or higher than the"
        + "solution list size " + solutionList.size());

    int next = solutionIndex == 0 ? solutionList.size() - 1 : solutionIndex - 1;
    int previous = (solutionIndex + 1) % solutionList.size();

    List<S> neighbourSolutions = new ArrayList<>();
    neighbourSolutions.add(solutionList.get(next));
    neighbourSolutions.add(solutionList.get(previous));

    return neighbourSolutions;
  }
}
