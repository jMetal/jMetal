package org.uma.jmetal.component.catalogue.ea.selection.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

/**
 * Selects solutions by their position in the list, following a {@link SequenceGenerator}: every
 * solution is selected exactly once per full cycle before any repeats. Plug in a {@link
 * org.uma.jmetal.util.sequencegenerator.impl.CyclicIntegerSequence} for a fixed sweep in
 * population order, or a {@link org.uma.jmetal.util.sequencegenerator.impl.RandomPermutationCycle}
 * for a sweep in a freshly-shuffled order each cycle (reshuffled once exhausted) -- unlike {@link
 * RandomSelection} with {@code withReplacement = false}, which only guarantees no repeats within a
 * single {@link #select(List)} call, not across the whole population before one repeats.
 *
 * <p>The sequence generator's length must match the size of the list passed to {@link
 * #select(List)}; this assumes a fixed-size population across calls, as in steady-state algorithms
 * (e.g. SMS-EMOA, MOEA/D), matching how {@link DifferentialEvolutionSelection} already uses a
 * {@link SequenceGenerator} in this same package.
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 *
 * @param <S>
 */
public class IterativeSelection<S extends Solution<?>> implements Selection<S> {

  private final int numberOfElementsToSelect;
  private final SequenceGenerator<Integer> sequenceGenerator;

  public IterativeSelection(
      int numberOfElementsToSelect, SequenceGenerator<Integer> sequenceGenerator) {
    this.numberOfElementsToSelect = numberOfElementsToSelect;
    this.sequenceGenerator = sequenceGenerator;
  }

  @Override
  public List<S> select(List<S> solutionList) {
    Check.that(
        solutionList.size() == sequenceGenerator.getSequenceLength(),
        "The solution list size "
            + solutionList.size()
            + " does not match the sequence generator length "
            + sequenceGenerator.getSequenceLength());

    List<S> selectedSolutions = new ArrayList<>(numberOfElementsToSelect);
    for (int i = 0; i < numberOfElementsToSelect; i++) {
      selectedSolutions.add(solutionList.get(sequenceGenerator.getValue()));
      sequenceGenerator.generateNext();
    }

    return selectedSolutions;
  }

  public int getNumberOfElementsToSelect() {
    return numberOfElementsToSelect;
  }
}
