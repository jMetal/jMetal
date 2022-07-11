package org.uma.jmetal.component.algorithm;

import java.util.List;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.selection.MatingPoolSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.Archive;

public class EvolutionaryAlgorithmWithExternalArchive<S extends Solution<?>>
    extends EvolutionaryAlgorithm<S> {

  private Archive<S> archive ;

  /**
   * Constructor
   *
   * @param name                      Algorithm name
   * @param initialPopulationCreation
   * @param evaluation
   * @param termination
   * @param selection
   * @param variation
   * @param replacement
   */
  public EvolutionaryAlgorithmWithExternalArchive(String name,
      SolutionsCreation<S> initialPopulationCreation, Evaluation<S> evaluation,
      Termination termination, MatingPoolSelection<S> selection, Variation<S> variation,
      Replacement<S> replacement, Archive<S> archive) {
    super(name, initialPopulationCreation, evaluation, termination, selection, variation,
        replacement);

    this.archive = archive ;
  }

  private void updateArchive() {
    for (S solution : getPopulation()) {
      archive.add(solution);
    }
  }

  @Override
  protected void initProgress() {
    super.initProgress();
    updateArchive();
  }

  @Override
  protected void updateProgress() {
    super.updateProgress();
    updateArchive();
  }

  @Override
  public List<S> getResult() {
    return archive.getSolutionList();
  }
}
