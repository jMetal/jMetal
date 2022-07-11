package org.uma.jmetal.component.algorithm.singleobjective;

import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.MuPlusLambdaReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.MatingPoolSelection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

/**
 * Class to configure and build an instance of a genetic  algorithm
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 *
 * @param <S>
 */
public class GeneticAlgorithmBuilder<S extends Solution<?>> {
  private String name;
  private Evaluation<S> evaluation;
  private SolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private MatingPoolSelection<S> selection;
  private Variation<S> variation;
  private Replacement<S> replacement;

  public GeneticAlgorithmBuilder(String name, Problem<S> problem, int populationSize,
      int offspringPopulationSize,
      CrossoverOperator<S> crossover, MutationOperator<S> mutation) {
    this.name = name;

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    this.replacement =
        new MuPlusLambdaReplacement<>(new ObjectiveComparator<>(0));

    this.variation =
        new CrossoverAndMutationVariation<>(
            offspringPopulationSize, crossover, mutation);

    this.selection =
        new NaryTournamentMatingPoolSelection<>(
            2,
            variation.getMatingPoolSize(),
            new ObjectiveComparator<>(0));

    this.termination = new TerminationByEvaluations(25000);

    this.evaluation = new SequentialEvaluation<>(problem);
  }

  public GeneticAlgorithmBuilder<S> setTermination(Termination termination) {
    this.termination = termination;

    return this;
  }

  public GeneticAlgorithmBuilder<S> setEvaluation(Evaluation<S> evaluation) {
    this.evaluation = evaluation;

    return this;
  }

  public GeneticAlgorithmBuilder<S> setReplacement(Replacement<S> replacement) {
    this.replacement = replacement;

    return this;
  }

  public GeneticAlgorithmBuilder<S> setSelection(MatingPoolSelection<S> selection) {
    this.selection = selection;

    return this;
  }

  public GeneticAlgorithmBuilder<S> setVariation(Variation<S> variation) {
    this.variation = variation;

    return this;
  }

  public EvolutionaryAlgorithm<S> build() {
    return new EvolutionaryAlgorithm<>(name, createInitialPopulation, evaluation, termination,
        selection, variation, replacement) {
      @Override
      public void updateProgress() {
        S bestFitnessSolution = getPopulation().stream().min(new ObjectiveComparator<>(0)).get();
        getAttributes().put("BEST_SOLUTION", bestFitnessSolution);

        super.updateProgress();
      }
    };
  }
}
