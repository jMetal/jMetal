package org.uma.jmetal.component.catalogue.ea.selection.impl;

import java.util.List;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ListUtils;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Randomly select a number of solutions from a list, with or without replacement
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 *
 * @param <S>
 */
public class RandomSelection<S extends Solution<?>> implements Selection<S> {

  private final int numberOfElementsToSelect;
  private final boolean withReplacement;
  private final BoundedRandomGenerator<Integer> randomGenerator;

  public RandomSelection(int numberOfElementsToSelect) {
    this(numberOfElementsToSelect, false);
  }

  public RandomSelection(int numberOfElementsToSelect, boolean withReplacement) {
    this(numberOfElementsToSelect, withReplacement, JMetalRandom.getInstance()::nextInt);
  }

  public RandomSelection(int numberOfElementsToSelect, boolean withReplacement,
      BoundedRandomGenerator<Integer> pseudoRandomGenerator) {
    this.numberOfElementsToSelect = numberOfElementsToSelect;
    this.withReplacement = withReplacement;
    this.randomGenerator = pseudoRandomGenerator;
  }

  public List<S> select(List<S> solutionList) {
    List<S> selectedSolutions;
    if (withReplacement) {
      selectedSolutions = ListUtils.randomSelectionWithoutReplacement(numberOfElementsToSelect,
          solutionList, randomGenerator);
    } else {
      selectedSolutions = ListUtils.randomSelectionWithReplacement(numberOfElementsToSelect,
          solutionList, randomGenerator);
    }

    return selectedSolutions;
  }

  public boolean selectionWithReplacement() {
    return withReplacement;
  }

  public int getNumberOfElementsToSelect() {
    return getNumberOfElementsToSelect();
  }
}
