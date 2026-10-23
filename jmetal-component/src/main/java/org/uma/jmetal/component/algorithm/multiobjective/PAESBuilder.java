package org.uma.jmetal.component.algorithm.multiobjective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.ConstraintHandlingReplacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.PAESReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.PAESSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.MutationOnlyVariation;
import org.uma.jmetal.component.util.RunProgress;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.constrainthandling.PairwiseConstraintHandlingTechnique;

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
  private PairwiseConstraintHandlingTechnique<S> constraintHandlingTechnique;

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

    // Deb's constrained-domination principle: plain Pareto dominance without constraints
    this.replacement = new PAESReplacement<>(archive, new DominanceWithConstraintsComparator<>());

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

  /**
   * Sets the constraint-handling technique, which decides whether the mutant replaces the current
   * solution (by default, Deb's constrained-domination principle). It is updated with the
   * progress of the run and with the archive, the current solution and the mutant once every
   * archive size iterations, the equivalent of a generation. Only pairwise techniques can be used,
   * because PAES compares two solutions. The archive, which is the result of the algorithm, keeps
   * comparing with the constrained-domination principle: the current solution can be a mutant
   * that the technique accepts and the archive does not. A technique has state, so it must not be
   * shared by several algorithms.
   *
   * @param technique the constraint-handling technique
   * @return this builder
   */
  public PAESBuilder<S> setConstraintHandlingTechnique(
      PairwiseConstraintHandlingTechnique<S> technique) {
    this.constraintHandlingTechnique = technique;

    return this;
  }

  public EvolutionaryAlgorithm<S> build() {
    RunProgress progress = new RunProgress();
    Replacement<S> algorithmReplacement = replacement;
    if (constraintHandlingTechnique != null) {
      algorithmReplacement =
          new ConstraintHandlingReplacement<>(
              constraintHandlingTechnique,
              new PAESReplacement<>(archive, constraintHandlingTechnique.comparator()),
              progress,
              archive.maximumSize(),
              (population, offspring) -> archiveAndCandidates(archive, population, offspring));
    }

    var algorithm = new PAESAlgorithm<>(
        name,
        createInitialPopulation,
        evaluation,
        termination,
        selection,
        variation,
        algorithmReplacement,
        archive);
    progress.bind(() -> algorithm.termination(), algorithm::attributes);

    return algorithm;
  }

  /** The archive, the current solution and the mutant, each solution once. */
  private static <S extends Solution<?>> List<S> archiveAndCandidates(
      BoundedArchive<S> archive, List<S> population, List<S> offspringPopulation) {
    Set<S> solutions = Collections.newSetFromMap(new IdentityHashMap<>());
    solutions.addAll(archive.solutions());
    solutions.addAll(population);
    solutions.addAll(offspringPopulation);
    return new ArrayList<>(solutions);
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
