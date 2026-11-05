package org.uma.jmetal.component.algorithm.multiobjective;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.ConstraintHandlingReplacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.SMSEMOAReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.RandomSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.component.util.RunProgress;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.constrainthandling.ConstraintHandlingTechnique;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.ranking.impl.MergeNonDominatedSortRanking;

/**
 * Class to configure and build an instance of the SMS-EMOA algorithm
 *
 * @param <S>
 */
public class SMSEMOABuilder<S extends Solution<?>> {
  private String name;
  private Ranking<S> ranking;
  private Evaluation<S> evaluation;
  private SolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private Selection<S> selection;
  private Variation<S> variation;
  private final int populationSize;
  private ConstraintHandlingTechnique<S> constraintHandlingTechnique;

  public SMSEMOABuilder(Problem<S> problem, int populationSize,
      CrossoverOperator<S> crossover, MutationOperator<S> mutation) {
    name = "SMS-EMOA";

    ranking = createDefaultRanking(problem);

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    this.populationSize = populationSize;

    this.variation =
        new CrossoverAndMutationVariation<>(1, crossover, mutation);

    this.selection = new RandomSelection<>(variation.matingPoolSize());

    this.termination = new TerminationByEvaluations(25000);

    this.evaluation = new SequentialEvaluation<>(problem);
  }

  public SMSEMOABuilder<S> setTermination(Termination termination) {
    this.termination = termination;

    return this;
  }

  @SuppressWarnings("deprecation")
  public SMSEMOABuilder<S> setRanking(Ranking<S> ranking) {
    this.ranking = ranking;

    return this;
  }

  /**
   * Sets the constraint-handling technique: the ranking of the replacement becomes {@code
   * technique.ranking()} (it replaces a ranking given with {@code setRanking}), and the technique
   * is updated with the progress of the run once every population size replacements, the
   * equivalent of a generation. Without a technique the algorithm behaves as before (its default
   * ranking ignores the constraints). The technique only guides the search: an external archive
   * keeps the feasible solutions with the constrained-domination principle, which is recommended
   * with {@link org.uma.jmetal.util.constrainthandling.StochasticRanking} and {@link
   * org.uma.jmetal.util.constrainthandling.SelfAdaptivePenalty}, whose final population can contain
   * infeasible solutions. A technique has state, so it must not be shared by several algorithms.
   */
  public SMSEMOABuilder<S> setConstraintHandlingTechnique(
      ConstraintHandlingTechnique<S> technique) {
    this.constraintHandlingTechnique = technique;

    return this;
  }

  public SMSEMOABuilder<S> setEvaluation(Evaluation<S> evaluation) {
    this.evaluation = evaluation;

    return this;
  }

  /**
   * Sets the creation of the initial population, which replaces the default random creation (for
   * example, to start from solutions computed with other algorithms). It must create exactly {@code
   * populationSize} solutions; otherwise the run fails with an {@link
   * org.uma.jmetal.util.errorchecking.exception.InvalidConditionException} when the initial
   * population is created.
   *
   * @param solutionsCreation the creation of the initial population
   * @return this builder
   */
  public SMSEMOABuilder<S> setCreateInitialPopulation(SolutionsCreation<S> solutionsCreation) {
    Check.notNull(solutionsCreation);
    this.createInitialPopulation = solutionsCreation;

    return this;
  }

  public EvolutionaryAlgorithm<S> build() {
    Ranking<S> algorithmRanking =
        constraintHandlingTechnique != null ? constraintHandlingTechnique.ranking() : ranking;
    RunProgress progress = new RunProgress();
    Replacement<S> replacement = new SMSEMOAReplacement<>(algorithmRanking);
    if (constraintHandlingTechnique != null) {
      replacement =
          new ConstraintHandlingReplacement<>(
              constraintHandlingTechnique, replacement, progress, populationSize);
    }

    var algorithm = new EvolutionaryAlgorithm<>(name,
        withPopulationSize(createInitialPopulation, populationSize), evaluation, termination,
        selection, variation, replacement);
    progress.bind(() -> algorithm.termination(), algorithm::attributes);

    return algorithm;
  }

  /**
   * Checks that the initial population has the size that the algorithm works with, and returns it
   * as a modifiable list, because the replacement updates the population in place.
   */
  private static <S extends Solution<?>> SolutionsCreation<S> withPopulationSize(
      SolutionsCreation<S> creation, int populationSize) {
    return () -> {
      List<S> population = creation.create();
      Check.that(
          population.size() == populationSize,
          () -> "The initial population has " + population.size()
              + " solutions, but the population size of SMS-EMOA is " + populationSize);
      return new ArrayList<>(population);
    };
  }

  private Ranking<S> createDefaultRanking(Problem<S> problem) {
    if (problem.numberOfObjectives() == 3) {
      return new FastNonDominatedSortRanking<>();
    }

    return new MergeNonDominatedSortRanking<>();
  }
}
