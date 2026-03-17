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
  private Selection<S> selection;
  private Variation<S> variation;
  private Replacement<S> replacement;

  public AGEMOEABuilder(Problem<S> problem, int populationSize, int offspringPopulationSize,
      CrossoverOperator<S> crossover, MutationOperator<S> mutation, Variant variant) {
    this.name = variant == Variant.AGEMOEA ? "AGE-MOEA" : "AGE-MOEA-II";
    this.variant = variant;
    this.problem = problem;
    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    AGEMOEAEnvironmentalSelection<S> environmentalSelection =
        variant == Variant.AGEMOEA
            ? new AGEMOEAEnvironmentalSelection<>(problem.numberOfObjectives())
            : new AGEMOEA2EnvironmentalSelection<>(problem.numberOfObjectives());

    this.replacement = new AGEMOEAReplacement<>(environmentalSelection);

    this.variation = new CrossoverAndMutationVariation<>(
        offspringPopulationSize, crossover, mutation);

    int tournamentSize = 2;
    this.selection = new NaryTournamentSelection<>(
        tournamentSize,
        variation.matingPoolSize(),
        new SurvivalScoreComparator<>());

    this.termination = new TerminationByEvaluations(25000);
    this.evaluation = new SequentialEvaluation<>(problem);
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
    this.createInitialPopulation = solutionsCreation;
    return this;
  }

  public AGEMOEABuilder<S> setSelection(Selection<S> selection) {
    this.selection = selection;
    return this;
  }

  public AGEMOEABuilder<S> setVariation(Variation<S> variation) {
    this.variation = variation;
    return this;
  }

  public EvolutionaryAlgorithm<S> build() {
    return new EvolutionaryAlgorithm<>(name, createInitialPopulation, evaluation, termination,
        selection, variation, replacement);
  }
}
