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
import org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea.AGEMOEA2EnvironmentalSelection;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea.AGEMOEAEnvironmentalSelection;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea.SurvivalScoreComparator;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Class to configure and build an instance of the AGE-MOEA or AGE-MOEA-II algorithm
 * using the component-based architecture.
 *
 * @param <S>
 */
public class AGEMOEABuilder<S extends Solution<?>> {
  public enum Variant {
    AGEMOEA,
    AGEMOEAII
  }

  private String name;
  private Variant variant;
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

  public AGEMOEABuilder(Problem<S> problem, int populationSize, int offspringPopulationSize,
      CrossoverOperator<S> crossover, MutationOperator<S> mutation, Variant variant) {
    this.name = variant == Variant.AGEMOEA ? "AGE-MOEA" : "AGE-MOEA-II";
    this.variant = variant;
    this.problem = problem;
    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    this.environmentalSelection = createEnvironmentalSelection();
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
   * before the first parent selection and, by default, in the AGE replacement.
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

  public EvolutionaryAlgorithm<S> build() {
    Selection<S> algorithmSelection =
        new AGEMOEASelection<>(selection, environmentalSelection);

    return new EvolutionaryAlgorithm<>(name, createInitialPopulation, evaluation, termination,
        algorithmSelection, variation, replacement);
  }

  private Selection<S> createDefaultSelection() {
    return new NaryTournamentSelection<>(
        selectionTournamentSize,
        variation.matingPoolSize(),
        new SurvivalScoreComparator<>());
  }

  private AGEMOEAEnvironmentalSelection<S> createEnvironmentalSelection() {
    return variant == Variant.AGEMOEA
        ? new AGEMOEAEnvironmentalSelection<>(problem.numberOfObjectives())
        : new AGEMOEA2EnvironmentalSelection<>(problem.numberOfObjectives());
  }
}
