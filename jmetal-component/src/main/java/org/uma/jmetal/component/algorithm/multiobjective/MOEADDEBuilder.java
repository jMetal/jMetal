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
import org.uma.jmetal.component.catalogue.ea.selection.MatingPoolSelection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.PopulationAndNeighborhoodMatingPoolSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.DifferentialCrossoverVariation;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.aggregativefunction.impl.Tschebyscheff;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.neighborhood.impl.WeightVectorNeighborhood;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

/**
 * Class to configure and build an instance of the MOEA/D-DE algorithm
 *
 */
public class MOEADDEBuilder {
  private String name;
  private Archive<DoubleSolution> externalArchive;
  private Evaluation<DoubleSolution> evaluation;
  private SolutionsCreation<DoubleSolution> createInitialPopulation;
  private Termination termination;
  private MatingPoolSelection<DoubleSolution> selection;
  private Variation<DoubleSolution> variation;
  private Replacement<DoubleSolution> replacement;

  private int neighborhoodSize = 20;
  private double neighborhoodSelectionProbability = 0.9;
  private int maximumNumberOfReplacedSolutions = 2;

  private AggregativeFunction aggregativeFunction = new Tschebyscheff();

  public MOEADDEBuilder(Problem<DoubleSolution> problem, int populationSize, double cr, double f,
      MutationOperator<DoubleSolution> mutation, String weightVectorDirectory,
      SequenceGenerator<Integer> sequenceGenerator) {
    name = "MOEADDE";

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    int offspringPopulationSize = 1;

    DifferentialEvolutionCrossover crossover =
        new DifferentialEvolutionCrossover(
            cr, f, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    this.variation =
        new DifferentialCrossoverVariation(
            offspringPopulationSize, crossover, mutation, sequenceGenerator);

    WeightVectorNeighborhood<DoubleSolution> neighborhood = null;

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

    this.selection =
        new PopulationAndNeighborhoodMatingPoolSelection<>(
            variation.getMatingPoolSize(),
            sequenceGenerator,
            neighborhood,
            neighborhoodSelectionProbability,
            true);

    this.replacement =
        new MOEADReplacement<>(
            (PopulationAndNeighborhoodMatingPoolSelection<DoubleSolution>) selection,
            neighborhood,
            aggregativeFunction,
            sequenceGenerator,
            maximumNumberOfReplacedSolutions);

    this.termination = new TerminationByEvaluations(25000);

    this.evaluation = new SequentialEvaluation<>(problem);

    this.externalArchive = null;
  }

  public MOEADDEBuilder setTermination(Termination termination) {
    this.termination = termination;

    return this;
  }

  public MOEADDEBuilder setArchive(Archive<DoubleSolution> externalArchive) {
    this.externalArchive = externalArchive;

    return this;
  }

  public MOEADDEBuilder setEvaluation(Evaluation<DoubleSolution> evaluation) {
    this.evaluation = evaluation;

    return this;
  }

  public MOEADDEBuilder setNeighborhoodSelectionProbability(
      double neighborhoodSelectionProbability) {
    this.neighborhoodSelectionProbability = neighborhoodSelectionProbability;

    return this;
  }

  public MOEADDEBuilder setMaximumNumberOfReplacedSolutionsy(int maximumNumberOfReplacedSolutions) {
    this.maximumNumberOfReplacedSolutions = maximumNumberOfReplacedSolutions;

    return this;
  }

  public MOEADDEBuilder setNeighborhoodSize(int neighborhoodSize) {
    this.neighborhoodSize = neighborhoodSize;

    return this;
  }

  public MOEADDEBuilder setAggregativeFunction(AggregativeFunction aggregativeFunction) {
    this.aggregativeFunction = aggregativeFunction;

    return this;
  }

  public MOEADDEBuilder setVariation(Variation<DoubleSolution> variation) {
    this.variation = variation;

    return this;
  }

  public MOEADDEBuilder setSelection(MatingPoolSelection<DoubleSolution> selection) {
    this.selection = selection;

    return this;
  }

  public EvolutionaryAlgorithm<DoubleSolution> build() {
    return new EvolutionaryAlgorithm<>(name, createInitialPopulation, evaluation, termination,
        selection, variation, replacement, externalArchive);
  }
}
