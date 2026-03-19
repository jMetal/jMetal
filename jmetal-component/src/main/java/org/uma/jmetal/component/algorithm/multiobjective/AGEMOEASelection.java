package org.uma.jmetal.component.algorithm.multiobjective;

import java.util.List;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea.AGEMOEAEnvironmentalSelection;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.solution.Solution;

/**
 * AGE-MOEA parent selection requires survival scores to be available before the first tournament.
 * This wrapper initializes them lazily for the first population and then delegates to the wrapped
 * selection component.
 *
 * @param <S> the solution type
 */
class AGEMOEASelection<S extends Solution<?>> implements Selection<S> {
  private final Selection<S> delegate;
  private final AGEMOEAEnvironmentalSelection<S> environmentalSelection;

  AGEMOEASelection(
      Selection<S> delegate, AGEMOEAEnvironmentalSelection<S> environmentalSelection) {
    this.delegate = delegate;
    this.environmentalSelection = environmentalSelection;
  }

  @Override
  public List<S> select(List<S> solutionList) {
    if (solutionList.stream().anyMatch(this::survivalScoreIsNotAssigned)) {
      environmentalSelection.execute(solutionList, solutionList.size());
    }

    return delegate.select(solutionList);
  }

  private boolean survivalScoreIsNotAssigned(S solution) {
    return !solution.attributes().containsKey(AGEMOEAEnvironmentalSelection.getAttributeId());
  }
}
