package org.uma.jmetal.component.algorithm.multiobjective;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.MOEADReplacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.MatingPoolSelection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.PopulationAndNeighborhoodMatingPoolSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.aggregativefunction.impl.PenaltyBoundaryIntersection;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.neighborhood.impl.WeightVectorNeighborhood;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerBoundedSequenceGenerator;
import org.uma.jmetal.util.termination.Termination;
import org.uma.jmetal.util.termination.impl.TerminationByEvaluations;

/**
 * Class to configure and build an instance of the NSGA-II algorithm
 *
 * @param <S>
 */
public class MOEADBuilder<S extends Solution<?>> {
  private String name;
  private Archive<S> externalArchive;
  private Evaluation<S> evaluation;
  private SolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private MatingPoolSelection<S> selection;
  private Variation<S> variation;
  private Replacement<S> replacement;

  private int neighborhoodSize ;
  private double neighborhoodSelectionProbability ;
  private int maximumNumberOfReplacedSolutions ;
  private String weightVectorDirectory ;
  private SequenceGenerator<Integer> sequenceGenerator ;
  private AggregativeFunction aggregativeFunction ;

  public MOEADBuilder(Problem<S> problem, int populationSize,
      CrossoverOperator<S> crossover, MutationOperator<S> mutation, String weightVectorDirectory) {
    name = "MOEAD";

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    int offspringPopulationSize = 1;
    this.variation =
        new CrossoverAndMutationVariation<>(
            offspringPopulationSize, crossover, mutation);

    WeightVectorNeighborhood<S> neighborhood = null;
    this.weightVectorDirectory = weightVectorDirectory ;

    neighborhoodSize = 20 ;
    if (problem.getNumberOfObjectives() == 2) {
      neighborhood = new WeightVectorNeighborhood<>(populationSize, neighborhoodSize);
    } else {
      try {
        neighborhood =
            new WeightVectorNeighborhood<>(
                populationSize,
                problem.getNumberOfObjectives(),
                neighborhoodSize,
                weightVectorDirectory);
      } catch (FileNotFoundException exception) {
        exception.printStackTrace();
      }
    }

    neighborhoodSelectionProbability=0.9 ;
    sequenceGenerator = new IntegerBoundedSequenceGenerator(populationSize);
    this.selection =
        new PopulationAndNeighborhoodMatingPoolSelection<S>(
            variation.getMatingPoolSize(),
            sequenceGenerator,
            neighborhood,
            neighborhoodSelectionProbability,
            true);

    maximumNumberOfReplacedSolutions = 2 ;
    aggregativeFunction = new PenaltyBoundaryIntersection() ;
    this.replacement =
        new MOEADReplacement(
            (PopulationAndNeighborhoodMatingPoolSelection<S>) selection,
            neighborhood,
            aggregativeFunction,
            sequenceGenerator,
            maximumNumberOfReplacedSolutions);

    this.termination = new TerminationByEvaluations(25000);

    this.evaluation = new SequentialEvaluation<>(problem);

    this.externalArchive = null;
  }

  public MOEADBuilder<S> setTermination(Termination termination) {
    this.termination = termination;

    return this;
  }

  public MOEADBuilder<S> setArchive(Archive<S> externalArchive) {
    this.externalArchive = externalArchive;

    return this;
  }


  public MOEADBuilder<S> setEvaluation(Evaluation<S> evaluation) {
    this.evaluation = evaluation;

    return this;
  }

  public EvolutionaryAlgorithm<S> build() {
    return new EvolutionaryAlgorithm<>(name, createInitialPopulation, evaluation, termination,
        selection, variation, replacement, externalArchive);
  }
}
