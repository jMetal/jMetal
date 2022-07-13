package org.uma.jmetal.component.catalogue.ea.selection.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Randomly select a number of solutions from a list
 *
 * @param <S>
 */
public class RandomSelection<S extends Solution<?>> implements Selection<S> {
  private final int numberOfElementsToSelect;

  public RandomSelection(int numberOfElementsToSelect) {
    this.numberOfElementsToSelect = numberOfElementsToSelect;
  }

  public List<S> select(List<S> solutionList) {
    Check.notNull(solutionList);

    List<S> matingPool = new ArrayList<>();
    IntStream.range(0, numberOfElementsToSelect)
        .forEach(
            i ->
                matingPool.add(
                    solutionList.get(JMetalRandom.getInstance().nextInt(0, solutionList.size()-1))));

    return matingPool;
  }
}
