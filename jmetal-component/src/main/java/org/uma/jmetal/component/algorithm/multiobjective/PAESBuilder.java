package org.uma.jmetal.component.algorithm.multiobjective;

import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.PAESReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.PAESSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.MutationOnlyVariation;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;

/**
 * Class to configure and build a component-based instance of the PAES (Pareto Archived Evolution
 * Strategy) algorithm.
 *
 * <p>PAES is a (1+1) evolution strategy: the working population is fixed at size 1, variation is
 * mutation-only (no crossover), and a bounded density archive acts both as the algorithm result and
 * as a tiebreaker in the replacement step. The replacement follows the three-way PAES rule: accept
 * the offspring if it dominates the current solution, reject it if dominated, or use the archive's
 * density comparator as a tiebreaker when neither solution dominates the other.
 *
 * <p>The {@code archiveSize} provided to the constructor plays a role analogous to the population
 * size of other algorithms (typical value: 100).
 *
 * @param <S> the solution type
 */
public class PAESBuilder<S extends Solution<?>> {
  private String name;
  private final BoundedArchive<S> archive;
  private double archiveSelectionProbability;
  private Evaluation<S> evaluation;
  private SolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private Selection<S> selection;
  private Variation<S> variation;
  private Replacement<S> replacement;

  public PAESBuilder(
      Problem<S> problem,
      int archiveSize,
      MutationOperator<S> mutation,
      BoundedArchive<S> archive) {
    this.name = "PAES";
    this.archive = archive;
    this.archiveSelectionProbability = 0.0;

    // A (1+1) ES starts from a single random solution.
    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, 1);

    this.variation = new MutationOnlyVariation<>(1, mutation);

    this.selection = new PAESSelection<>(archiveSelectionProbability, archive);

    this.replacement = new PAESReplacement<>(archive, new DefaultDominanceComparator<>());

    this.termination = new TerminationByEvaluations(25000);

    this.evaluation = new SequentialEvaluation<>(problem);
  }

  public PAESBuilder<S> setName(String name) {
    this.name = name;

    return this;
  }

  public PAESBuilder<S> setTermination(Termination termination) {
    this.termination = termination;

    return this;
  }

  public PAESBuilder<S> setEvaluation(Evaluation<S> evaluation) {
    this.evaluation = evaluation;

    return this;
  }

  public PAESBuilder<S> setArchiveSelectionProbability(double archiveSelectionProbability) {
    this.archiveSelectionProbability = archiveSelectionProbability;
    this.selection = new PAESSelection<>(archiveSelectionProbability, archive);

    return this;
  }

  public EvolutionaryAlgorithm<S> build() {
    return new PAESAlgorithm<>(
        name,
        createInitialPopulation,
        evaluation,
        termination,
        selection,
        variation,
        replacement,
        archive);
  }

  /**
   * Extends {@link EvolutionaryAlgorithm} to return the PAES archive contents as the result instead
   * of the population (which has size 1 and is not the intended output).
   */
  private static class PAESAlgorithm<S extends Solution<?>> extends EvolutionaryAlgorithm<S> {
    private final BoundedArchive<S> archive;

    public PAESAlgorithm(
        String name,
        SolutionsCreation<S> createInitialPopulation,
        Evaluation<S> evaluation,
        Termination termination,
        Selection<S> selection,
        Variation<S> variation,
        Replacement<S> replacement,
        BoundedArchive<S> archive) {
      super(name, createInitialPopulation, evaluation, termination, selection, variation,
          replacement);
      this.archive = archive;
    }

    @Override
    public List<S> result() {
      return archive.solutions();
    }
  }
}
