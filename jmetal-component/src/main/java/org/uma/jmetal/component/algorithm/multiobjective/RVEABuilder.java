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
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RVEAReplacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea.RVEAEnvironmentalSelection;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.RandomSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.referencepoint.ReferencePointGenerator;

/**
 * Class to configure and build an instance of the RVEA (Reference Vector Guided Evolutionary Algorithm)
 * using the component-based architecture.
 *
 * @param <S>
 */
public class RVEABuilder<S extends Solution<?>> {
  private final String name;
  private final Problem<S> problem;
  private final int populationSize;
  private final double alpha;
  private final double fr;
  private final int numberOfDivisions;
  private List<double[]> referenceVectors;
  private Evaluation<S> evaluation;
  private SolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private Selection<S> selection;
  private Variation<S> variation;
  private Replacement<S> replacement;
  private boolean customSelection;

  public RVEABuilder(Problem<S> problem, int populationSize, int maxEvaluations,
      CrossoverOperator<S> crossover, MutationOperator<S> mutation, double alpha, double fr, int h) {
    this.name = "RVEA";
    this.problem = problem;
    this.populationSize = populationSize;
    this.alpha = alpha;
    this.fr = fr;
    this.numberOfDivisions = h;
    this.referenceVectors =
        ReferencePointGenerator.generateSingleLayer(problem.numberOfObjectives(), numberOfDivisions);
    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    this.variation = new CrossoverAndMutationVariation<>(
        populationSize, crossover, mutation);

    this.selection = new RandomSelection<>(variation.matingPoolSize());
    this.customSelection = false;

    this.termination = new TerminationByEvaluations(maxEvaluations);
    this.evaluation = new SequentialEvaluation<>(problem);
  }

  public RVEABuilder<S> setTermination(Termination termination) {
    this.termination = termination;
    return this;
  }

  public RVEABuilder<S> setEvaluation(Evaluation<S> evaluation) {
    this.evaluation = evaluation;
    return this;
  }

  public RVEABuilder<S> setCreateInitialPopulation(SolutionsCreation<S> solutionsCreation) {
    this.createInitialPopulation = solutionsCreation;
    return this;
  }

  public RVEABuilder<S> setSelection(Selection<S> selection) {
    this.selection = selection;
    this.customSelection = true;
    return this;
  }

  public RVEABuilder<S> setVariation(Variation<S> variation) {
    this.variation = variation;
    return this;
  }

  /**
   * Overrides the paper-default single-layer reference vector set with a caller-provided one.
   * This advanced hook is intended for experimental configurations and benchmark helpers.
   *
   * @param referenceVectors Reference vectors to be used by environmental selection.
   * @return The builder instance.
   */
  public RVEABuilder<S> setReferenceVectors(List<double[]> referenceVectors) {
    Check.notNull(referenceVectors);

    this.referenceVectors = new ArrayList<>(referenceVectors.size());
    for (double[] referenceVector : referenceVectors) {
      this.referenceVectors.add(referenceVector.clone());
    }

    return this;
  }

  /**
   * Overrides the paper-default single-layer reference vector set with a caller-provided one.
   * This variant accepts a 2D array for convenient initialization.
   *
   * @param referenceVectors Reference vectors to be used by environmental selection.
   * @return The builder instance.
   */
  public RVEABuilder<S> setReferenceVectors(double[][] referenceVectors) {
    Check.notNull(referenceVectors);

    this.referenceVectors = new ArrayList<>(referenceVectors.length);
    for (double[] referenceVector : referenceVectors) {
      this.referenceVectors.add(referenceVector.clone());
    }

    return this;
  }

  public EvolutionaryAlgorithm<S> build() {
    Check.that(termination instanceof TerminationByEvaluations,
        "RVEA requires termination by evaluations to compute APD progress and reference adaptation.");
    Check.notNull(referenceVectors);

    int referenceVectorCount = referenceVectors.size();
    Check.that(referenceVectorCount == populationSize,
        "Population size must match the number of generated reference vectors. Expected "
            + referenceVectorCount + " and found " + populationSize + ".");

    int maxEvaluations = ((TerminationByEvaluations) termination).getMaximumNumberOfEvaluations();
    int maxGenerations = estimateMaximumGenerations(maxEvaluations, populationSize,
        variation.offspringPopulationSize());
    RVEAEnvironmentalSelection<S> environmentalSelection =
        new RVEAEnvironmentalSelection<>(problem.numberOfObjectives(), maxGenerations, alpha, fr,
            referenceVectors);
    replacement = new RVEAReplacement<>(environmentalSelection);

    Selection<S> finalSelection =
        customSelection ? selection : new RandomSelection<>(variation.matingPoolSize());
    SolutionsCreation<S> validatedInitialPopulationCreation = () -> {
      var initialPopulation = createInitialPopulation.create();
      Check.that(initialPopulation.size() == populationSize,
          "The initial population size must be " + populationSize + " but is "
              + initialPopulation.size() + ".");
      return initialPopulation;
    };

    return new EvolutionaryAlgorithm<>(name, validatedInitialPopulationCreation, evaluation,
        termination, finalSelection, variation, replacement);
  }

  private int estimateMaximumGenerations(int maxEvaluations, int initialPopulationSize,
      int offspringPopulationSize) {
    Check.valueIsPositive(offspringPopulationSize, "offspringPopulationSize");

    if (maxEvaluations <= initialPopulationSize) {
      return 1;
    }

    return (int) Math.ceil((double) (maxEvaluations - initialPopulationSize) / offspringPopulationSize);
  }
}
