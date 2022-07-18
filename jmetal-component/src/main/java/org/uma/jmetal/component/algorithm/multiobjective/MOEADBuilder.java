package org.uma.jmetal.component.algorithm.multiobjective;

import java.io.FileNotFoundException;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.MOEADReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.PopulationAndNeighborhoodSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.aggregativefunction.impl.PenaltyBoundaryIntersection;
import org.uma.jmetal.util.neighborhood.impl.WeightVectorNeighborhood;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

/**
 * Class to configure and build an instance of the MOEA/D algorithm
 *
 * @param <S>
 */
public class MOEADBuilder<S extends Solution<?>> {
  private String name;
  private Evaluation<S> evaluation;
  private SolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private Selection<S> selection;
  private Variation<S> variation;
  private Replacement<S> replacement;

  private int neighborhoodSize = 20 ;
  private double neighborhoodSelectionProbability = 0.9 ;
  private int maximumNumberOfReplacedSolutions = 2;
  private String weightVectorDirectory ;
  private AggregativeFunction aggregativeFunction = new PenaltyBoundaryIntersection() ;

  public MOEADBuilder(@NotNull Problem<S> problem, int populationSize,
                      @NotNull CrossoverOperator<S> crossover, MutationOperator<S> mutation, String weightVectorDirectory,
                      SequenceGenerator<Integer> sequenceGenerator) {
    name = "MOEAD";

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    int offspringPopulationSize = 1;
    this.variation =
        new CrossoverAndMutationVariation<>(
            offspringPopulationSize, crossover, mutation);

    WeightVectorNeighborhood<S> neighborhood = null;
    this.weightVectorDirectory = weightVectorDirectory ;

    if (problem.getNumberOfObjectives() == 2) {
      neighborhood = new WeightVectorNeighborhood<>(populationSize, neighborhoodSize);
    } else {
      try {
        neighborhood =
            new WeightVectorNeighborhood<>(
                populationSize,
                problem.getNumberOfObjectives(),
                neighborhoodSize,
                this.weightVectorDirectory);
      } catch (FileNotFoundException exception) {
        exception.printStackTrace();
      }
    }

    this.selection =
        new PopulationAndNeighborhoodSelection<>(
            variation.getMatingPoolSize(),
            sequenceGenerator,
            neighborhood,
            neighborhoodSelectionProbability,
            true);

    this.replacement =
        new MOEADReplacement<>(
            (PopulationAndNeighborhoodSelection<S>) selection,
            neighborhood,
            aggregativeFunction,
            sequenceGenerator,
            maximumNumberOfReplacedSolutions);

    this.termination = new TerminationByEvaluations(25000);

    this.evaluation = new SequentialEvaluation<>(problem);
  }

  public MOEADBuilder<S> setTermination(Termination termination) {
    this.termination = termination;

    return this;
  }

  public @NotNull MOEADBuilder<S> setEvaluation(Evaluation<S> evaluation) {
    this.evaluation = evaluation;

    return this;
  }

  public @NotNull MOEADBuilder<S> setNeighborhoodSelectionProbability(double neighborhoodSelectionProbability) {
    this.neighborhoodSelectionProbability = neighborhoodSelectionProbability;

    return this;
  }

  public @NotNull MOEADBuilder<S> setMaximumNumberOfReplacedSolutionsy(int maximumNumberOfReplacedSolutions) {
    this.maximumNumberOfReplacedSolutions = maximumNumberOfReplacedSolutions;

    return this;
  }

  public MOEADBuilder<S> setNeighborhoodSize(int neighborhoodSize) {
    this.neighborhoodSize = neighborhoodSize;

    return this;
  }

  public MOEADBuilder<S> setAggregativeFunction(AggregativeFunction aggregativeFunction) {
    this.aggregativeFunction = aggregativeFunction;

    return this;
  }

  public MOEADBuilder<S> setVariation(Variation<S> variation) {
    this.variation = variation;

    return this;
  }

  public MOEADBuilder<S> setSelection(Selection<S> selection) {
    this.selection = selection;

    return this;
  }

  public EvolutionaryAlgorithm<S> build() {
    return new EvolutionaryAlgorithm<>(name, createInitialPopulation, evaluation, termination,
        selection, variation, replacement);
  }
}
