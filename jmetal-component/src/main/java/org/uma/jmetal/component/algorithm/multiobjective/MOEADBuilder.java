package org.uma.jmetal.component.algorithm.multiobjective;

import java.io.FileNotFoundException;
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
import org.uma.jmetal.util.aggregationfunction.AggregationFunction;
import org.uma.jmetal.util.aggregationfunction.impl.PenaltyBoundaryIntersection;
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
  private int neighborhoodSize = 20;
  private double neighborhoodSelectionProbability = 0.9;
  private int maximumNumberOfReplacedSolutions = 2;
  private String weightVectorDirectory;
  private AggregationFunction aggregationFunction;
  private SequenceGenerator<Integer> sequenceGenerator;
  private WeightVectorNeighborhood<S> neighborhood;
  private boolean normalize;

  public MOEADBuilder(Problem<S> problem, int populationSize,
      CrossoverOperator<S> crossover, MutationOperator<S> mutation, String weightVectorDirectory,
      SequenceGenerator<Integer> sequenceGenerator, boolean normalize) {
    name = "MOEAD";

    this.normalize = normalize;
    this.aggregationFunction = new PenaltyBoundaryIntersection(5.0, normalize);

    this.sequenceGenerator = sequenceGenerator;
    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    int offspringPopulationSize = 1;
    this.variation =
        new CrossoverAndMutationVariation<>(
            offspringPopulationSize, crossover, mutation);

    this.weightVectorDirectory = weightVectorDirectory;

    if (problem.numberOfObjectives() == 2) {
      neighborhood = new WeightVectorNeighborhood<>(populationSize, neighborhoodSize);
    } else {
      try {
        neighborhood =
            new WeightVectorNeighborhood<>(
                populationSize,
                problem.numberOfObjectives(),
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
            aggregationFunction,
            sequenceGenerator,
            maximumNumberOfReplacedSolutions,
            this.normalize);

    this.termination = new TerminationByEvaluations(25000);

    this.evaluation = new SequentialEvaluation<>(problem);
  }

  public MOEADBuilder<S> setTermination(Termination termination) {
    this.termination = termination;

    return this;
  }

  public MOEADBuilder<S> setEvaluation(Evaluation<S> evaluation) {
    this.evaluation = evaluation;

    return this;
  }

  public MOEADBuilder<S> setNeighborhoodSelectionProbability(
      double neighborhoodSelectionProbability) {
    this.neighborhoodSelectionProbability = neighborhoodSelectionProbability;

    return this;
  }

  public MOEADBuilder<S> setMaximumNumberOfReplacedSolutionsy(
      int maximumNumberOfReplacedSolutions) {
    this.maximumNumberOfReplacedSolutions = maximumNumberOfReplacedSolutions;

    return this;
  }

  public MOEADBuilder<S> setNeighborhoodSize(int neighborhoodSize) {
    this.neighborhoodSize = neighborhoodSize;

    return this;
  }

  public MOEADBuilder<S> setAggregationFunction(AggregationFunction aggregationFunction) {
    this.aggregationFunction = aggregationFunction;

    this.replacement =
        new MOEADReplacement<>(
            (PopulationAndNeighborhoodSelection<S>) selection,
            neighborhood,
            this.aggregationFunction,
            sequenceGenerator,
            maximumNumberOfReplacedSolutions,
            aggregationFunction.normalizeObjectives());
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
