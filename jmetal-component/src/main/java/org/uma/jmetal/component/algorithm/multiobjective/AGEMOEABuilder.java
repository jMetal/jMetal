package org.uma.jmetal.component.algorithm.multiobjective;

import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.AGEMOEAReplacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.ConstraintHandlingReplacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea.AGEMOEA2EnvironmentalSelection;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea.AGEMOEAEnvironmentalSelection;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea.SurvivalScoreComparator;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.component.util.RunProgress;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.constrainthandling.ConstraintHandlingTechnique;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.ranking.Ranking;

/**
 * Class to configure and build an instance of the AGE-MOEA or AGE-MOEA-II algorithm
 * using the component-based architecture. The specific algorithm variant is determined
 * by the {@link AGEMOEAEnvironmentalSelection} component injected via the constructor:
 * use {@link AGEMOEAEnvironmentalSelection} for AGE-MOEA and
 * {@link AGEMOEA2EnvironmentalSelection} for AGE-MOEA-II.
 *
 * @param <S>
 */
public class AGEMOEABuilder<S extends Solution<?>> {
  private String name;
  private Problem<S> problem;
  private Evaluation<S> evaluation;
  private SolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private int selectionTournamentSize;
  private Selection<S> selection;
  private Variation<S> variation;
  private AGEMOEAEnvironmentalSelection<S> environmentalSelection;
  private Replacement<S> replacement;
  private boolean customSelectionConfigured;
  private boolean customReplacementConfigured;
  private ConstraintHandlingTechnique<S> constraintHandlingTechnique;

  public AGEMOEABuilder(Problem<S> problem, int populationSize, int offspringPopulationSize,
      CrossoverOperator<S> crossover, MutationOperator<S> mutation,
      AGEMOEAEnvironmentalSelection<S> environmentalSelection) {
    this.name = environmentalSelection instanceof AGEMOEA2EnvironmentalSelection
        ? "AGE-MOEA-II" : "AGE-MOEA";
    this.problem = problem;
    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    this.environmentalSelection = environmentalSelection;
    this.replacement = new AGEMOEAReplacement<>(environmentalSelection);

    this.variation = new CrossoverAndMutationVariation<>(
        offspringPopulationSize, crossover, mutation);

    this.selectionTournamentSize = 2;
    this.selection = createDefaultSelection();

    this.termination = new TerminationByEvaluations(25000);
    this.evaluation = new SequentialEvaluation<>(problem);
    this.customSelectionConfigured = false;
    this.customReplacementConfigured = false;
  }

  public AGEMOEABuilder<S> setTermination(Termination termination) {
    this.termination = termination;
    return this;
  }

  public AGEMOEABuilder<S> setEvaluation(Evaluation<S> evaluation) {
    this.evaluation = evaluation;
    return this;
  }

  public AGEMOEABuilder<S> setCreateInitialPopulation(SolutionsCreation<S> solutionsCreation) {
    Check.notNull(solutionsCreation, "solutionsCreation");
    this.createInitialPopulation = solutionsCreation;
    return this;
  }

  /**
   * Configures the tournament size used by the default AGE-MOEA parent selection.
   * If a custom selection component was already provided, this value is stored but
   * does not override that custom selection.
   *
   * @param tournamentSize number of competitors in each tournament
   * @return this builder
   */
  public AGEMOEABuilder<S> setTournamentSize(int tournamentSize) {
    Check.that(tournamentSize >= 2, "Tournament size must be greater than or equal to 2");

    this.selectionTournamentSize = tournamentSize;
    if (!customSelectionConfigured) {
      this.selection = createDefaultSelection();
    }

    return this;
  }

  public AGEMOEABuilder<S> setSelection(Selection<S> selection) {
    Check.notNull(selection, "selection");
    this.selection = selection;
    this.customSelectionConfigured = true;
    return this;
  }

  public AGEMOEABuilder<S> setVariation(Variation<S> variation) {
    Check.notNull(variation, "variation");
    this.variation = variation;
    if (!customSelectionConfigured) {
      this.selection = createDefaultSelection();
    }
    return this;
  }

  /**
   * Configures the AGE environmental selection used to assign the survival scores
   * before the first parent selection and, by default, in the AGE replacement. The default
   * parent selection compares the ranks of its {@link AGEMOEAEnvironmentalSelection#ranking()}.
   *
   * @param environmentalSelection environmental selection strategy
   * @return this builder
   */
  public AGEMOEABuilder<S> setEnvironmentalSelection(
      AGEMOEAEnvironmentalSelection<S> environmentalSelection) {
    Check.notNull(environmentalSelection, "environmentalSelection");

    this.environmentalSelection = environmentalSelection;
    if (!customReplacementConfigured) {
      this.replacement = new AGEMOEAReplacement<>(environmentalSelection);
    }
    if (!customSelectionConfigured) {
      this.selection = createDefaultSelection();
    }

    return this;
  }

  /**
   * Configures the replacement component.
   *
   * @param replacement replacement strategy
   * @return this builder
   */
  public AGEMOEABuilder<S> setReplacement(Replacement<S> replacement) {
    Check.notNull(replacement, "replacement");

    this.replacement = replacement;
    this.customReplacementConfigured = true;

    return this;
  }

  /**
   * Sets the constraint-handling technique: the environmental selection (of AGE-MOEA or
   * AGE-MOEA-II, as the one the builder has) ranks the population with {@code
   * technique.ranking()}, the default parent selection compares those ranks, and the technique is
   * updated once per generation with the progress of the run. Without a technique the algorithm
   * behaves as before (it ranks with the constrained-domination principle). The technique only
   * guides the search: an external archive keeps the feasible solutions with the
   * constrained-domination principle, which is recommended with {@link
   * org.uma.jmetal.util.constrainthandling.StochasticRanking} and {@link
   * org.uma.jmetal.util.constrainthandling.SelfAdaptivePenalty}, whose final population can contain
   * infeasible solutions. A technique has state, so it must not be shared by several algorithms.
   *
   * @param technique the constraint-handling technique
   * @return this builder
   */
  public AGEMOEABuilder<S> setConstraintHandlingTechnique(
      ConstraintHandlingTechnique<S> technique) {
    this.constraintHandlingTechnique = technique;
    return this;
  }

  public EvolutionaryAlgorithm<S> build() {
    AGEMOEAEnvironmentalSelection<S> algorithmEnvironmentalSelection =
        constraintHandlingTechnique != null
            ? environmentalSelectionWith(constraintHandlingTechnique.ranking())
            : environmentalSelection;
    RunProgress progress = new RunProgress();
    Replacement<S> algorithmReplacement =
        customReplacementConfigured
            ? replacement
            : new AGEMOEAReplacement<>(algorithmEnvironmentalSelection);
    if (constraintHandlingTechnique != null) {
      algorithmReplacement =
          new ConstraintHandlingReplacement<>(
              constraintHandlingTechnique, algorithmReplacement, progress);
    }
    Selection<S> parentSelection =
        customSelectionConfigured
            ? selection
            : createDefaultSelection(algorithmEnvironmentalSelection);
    Selection<S> algorithmSelection =
        new AGEMOEASelection<>(parentSelection, algorithmEnvironmentalSelection);

    var algorithm = new EvolutionaryAlgorithm<>(name, createInitialPopulation, evaluation,
        termination, algorithmSelection, variation, algorithmReplacement);
    progress.bind(() -> algorithm.termination(), algorithm::attributes);

    return algorithm;
  }

  /** An environmental selection of the same kind as the one of the builder, with a ranking. */
  private AGEMOEAEnvironmentalSelection<S> environmentalSelectionWith(Ranking<S> ranking) {
    int numberOfObjectives = problem.numberOfObjectives();
    return environmentalSelection instanceof AGEMOEA2EnvironmentalSelection
        ? new AGEMOEA2EnvironmentalSelection<>(numberOfObjectives, ranking)
        : new AGEMOEAEnvironmentalSelection<>(numberOfObjectives, ranking);
  }

  private Selection<S> createDefaultSelection() {
    return createDefaultSelection(environmentalSelection);
  }

  private Selection<S> createDefaultSelection(
      AGEMOEAEnvironmentalSelection<S> environmentalSelection) {
    return new NaryTournamentSelection<>(
        selectionTournamentSize,
        variation.matingPoolSize(),
        new SurvivalScoreComparator<>(environmentalSelection.ranking()));
  }

}
