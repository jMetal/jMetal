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
import org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.SubproblemUpdateCriterion;
import org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.impl.AggregationCriterion;
import org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.impl.ConstraintHandlingCriterion;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.PopulationAndNeighborhoodSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.DifferentialEvolutionCrossoverVariation;
import org.uma.jmetal.component.util.RunProgress;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.aggregationfunction.AggregationFunction;
import org.uma.jmetal.util.aggregationfunction.impl.Tschebyscheff;
import org.uma.jmetal.util.constrainthandling.PairwiseConstraintHandlingTechnique;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.neighborhood.impl.WeightVectorNeighborhood;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

/**
 * Class to configure and build an instance of the MOEA/D-DE algorithm.
 *
 * <p>The neighborhood, the selection and the replacement, which keep state, are created when the
 * algorithm is built from the current values of the parameters, so the order of the calls of the
 * setters does not matter and every algorithm has its own components. The sequence generator and
 * the subproblem update criterion that are given to the builder are not copied: they are shared
 * by the algorithms that it builds.
 */
public class MOEADDEBuilder {

  private String name;
  private Evaluation<DoubleSolution> evaluation;
  private SolutionsCreation<DoubleSolution> createInitialPopulation;
  private Termination termination;
  private Selection<DoubleSolution> selection;
  private Variation<DoubleSolution> variation;
  private final double cr;
  private final double f;
  private final MutationOperator<DoubleSolution> mutation;
  private final int populationSize;
  private final int numberOfObjectives;
  private int neighborhoodSize = 20;
  private double neighborhoodSelectionProbability = 0.9;
  private int maximumNumberOfReplacedSolutions = 2;
  private final String weightVectorDirectory;
  private AggregationFunction aggregationFunction;
  private final SequenceGenerator<Integer> sequenceGenerator;
  private SubproblemUpdateCriterion<DoubleSolution> subproblemUpdateCriterion = new AggregationCriterion<>();
  private PairwiseConstraintHandlingTechnique<DoubleSolution> constraintHandlingTechnique;
  private boolean normalize;

  public MOEADDEBuilder(Problem<DoubleSolution> problem, int populationSize, double cr, double f,
      MutationOperator<DoubleSolution> mutation, String weightVectorDirectory,
      SequenceGenerator<Integer> sequenceGenerator, boolean normalize) {
    name = "MOEADDE";

    this.populationSize = populationSize;
    this.numberOfObjectives = problem.numberOfObjectives();
    this.normalize = normalize;
    this.aggregationFunction = new Tschebyscheff(normalize);

    this.sequenceGenerator = sequenceGenerator;
    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    this.cr = cr;
    this.f = f;
    this.mutation = mutation;

    this.weightVectorDirectory = weightVectorDirectory;

    this.termination = new TerminationByEvaluations(25000);

    this.evaluation = new SequentialEvaluation<>(problem);
  }

  public MOEADDEBuilder setTermination(Termination termination) {
    this.termination = termination;

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

  public MOEADDEBuilder setMaximumNumberOfReplacedSolutions(
      int maximumNumberOfReplacedSolutions) {
    this.maximumNumberOfReplacedSolutions = maximumNumberOfReplacedSolutions;

    return this;
  }

  public MOEADDEBuilder setNeighborhoodSize(int neighborhoodSize) {
    this.neighborhoodSize = neighborhoodSize;

    return this;
  }

  public MOEADDEBuilder setAggregationFunction(AggregationFunction aggregationFunction) {
    this.aggregationFunction = aggregationFunction;
    this.normalize = aggregationFunction.normalizeObjectives();

    return this;
  }

  /**
   * Sets the constraint-handling technique, which decides whether a new solution replaces the
   * solution of a subproblem: by the constraints of both and, when they do not decide, by their
   * values of the aggregation function (see {@link ConstraintHandlingCriterion}). It replaces the
   * subproblem update criterion, and is updated with the population and the progress of the run
   * once per generation. Only pairwise techniques can be used, because MOEA/D compares two
   * solutions of a subproblem. A technique has state, so it must not be shared by several
   * algorithms.
   *
   * @param technique the constraint-handling technique
   * @return this builder
   */
  public MOEADDEBuilder setConstraintHandlingTechnique(
      PairwiseConstraintHandlingTechnique<DoubleSolution> technique) {
    this.constraintHandlingTechnique = technique;

    return this;
  }

  /**
   * Sets the subproblem update criterion, which replaces the constraint-handling technique if one
   * was set.
   */
  public MOEADDEBuilder setSubproblemUpdateCriterion(
      SubproblemUpdateCriterion<DoubleSolution> subproblemUpdateCriterion) {
    this.subproblemUpdateCriterion = subproblemUpdateCriterion;
    this.constraintHandlingTechnique = null;

    return this;
  }

  /**
   * Sets the variation. If it is not set, differential evolution (DE/rand/1/bin) with the crossover
   * probability and the factor of the builder is used, and the sequence generator that gives the
   * solution to which it is applied is the one of the builder.
   */
  public MOEADDEBuilder setVariation(Variation<DoubleSolution> variation) {
    this.variation = variation;

    return this;
  }

  /**
   * Sets the selection. It has to be a {@link PopulationAndNeighborhoodSelection}, because the
   * replacement of MOEA/D reads the kind of neighbor that it chose. If it is not set, the selection
   * is built with the neighborhood size and the neighborhood selection probability of the builder.
   */
  public MOEADDEBuilder setSelection(Selection<DoubleSolution> selection) {
    this.selection = selection;

    return this;
  }

  private WeightVectorNeighborhood<DoubleSolution> createNeighborhood() {
    if (numberOfObjectives == 2) {
      return new WeightVectorNeighborhood<>(populationSize, neighborhoodSize);
    }

    try {
      return new WeightVectorNeighborhood<>(
          populationSize, numberOfObjectives, neighborhoodSize, weightVectorDirectory);
    } catch (FileNotFoundException exception) {
      throw new JMetalException(
          "Weight vector files not found in directory: " + weightVectorDirectory, exception);
    }
  }

  public EvolutionaryAlgorithm<DoubleSolution> build() {
    RunProgress progress = new RunProgress();
    WeightVectorNeighborhood<DoubleSolution> neighborhood = createNeighborhood();

    Variation<DoubleSolution> algorithmVariation =
        variation != null
            ? variation
            : new DifferentialEvolutionCrossoverVariation(
                1,
                new DifferentialEvolutionCrossover(
                    cr, f, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN),
                mutation,
                sequenceGenerator);

    Selection<DoubleSolution> algorithmSelection =
        selection != null
            ? selection
            : new PopulationAndNeighborhoodSelection<>(
                algorithmVariation.matingPoolSize(),
                sequenceGenerator,
                neighborhood,
                neighborhoodSelectionProbability,
                true);
    Check.that(
        algorithmSelection instanceof PopulationAndNeighborhoodSelection<DoubleSolution>,
        "The selection of MOEA/D must be a PopulationAndNeighborhoodSelection");

    SubproblemUpdateCriterion<DoubleSolution> criterion =
        constraintHandlingTechnique != null
            ? new ConstraintHandlingCriterion<>(constraintHandlingTechnique, progress)
            : subproblemUpdateCriterion;

    Replacement<DoubleSolution> replacement =
        new MOEADReplacement<>(
            (PopulationAndNeighborhoodSelection<DoubleSolution>) algorithmSelection,
            neighborhood,
            aggregationFunction,
            sequenceGenerator,
            maximumNumberOfReplacedSolutions,
            normalize,
            criterion);

    var algorithm = new EvolutionaryAlgorithm<>(name, createInitialPopulation, evaluation,
        termination, algorithmSelection, algorithmVariation, replacement);
    progress.bind(() -> algorithm.termination(), algorithm::attributes);

    return algorithm;
  }
}
