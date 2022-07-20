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
    this(numberOfElementsToSelect, true);
  }

  public RandomSelection(int numberOfElementsToSelect, boolean withReplacement) {
    this(numberOfElementsToSelect, withReplacement, JMetalRandom.getInstance()::nextInt);
  }

  /**
   * Constructor
   *
   * The check of invalid parameter values are omitted assuming that they are applied in the
   * {@link ListUtils#randomSelectionWithoutReplacement(int, List)} and
   * {@link ListUtils#randomSelectionWithReplacement(int, List)} methods used in the implementation
   * of method {@link #select(List)}
   *
   * @param numberOfElementsToSelect
   * @param withReplacement
   * @param pseudoRandomGenerator
   */
  public RandomSelection(int numberOfElementsToSelect, boolean withReplacement,
      BoundedRandomGenerator<Integer> pseudoRandomGenerator) {
    this.numberOfElementsToSelect = numberOfElementsToSelect;
    this.withReplacement = withReplacement;
    this.randomGenerator = pseudoRandomGenerator;
  }

  public List<S> select(List<S> solutionList) {
    List<S> selectedSolutions;
    if (withReplacement) {
      selectedSolutions = ListUtils.randomSelectionWithReplacement(numberOfElementsToSelect,
          solutionList, randomGenerator);
    } else {
      selectedSolutions = ListUtils.randomSelectionWithoutReplacement(numberOfElementsToSelect,
          solutionList, randomGenerator);
    }

    return selectedSolutions;
  }

  public boolean withReplacement() {
    return withReplacement;
  }

  public int getNumberOfElementsToSelect() {
    return numberOfElementsToSelect;
  }
}
