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
 * <p>Two constructor modes are supported:
 * <ul>
 *   <li>Provide {@code h} to generate paper-default reference vectors from the number of objectives.</li>
 *   <li>Provide preloaded reference vectors directly when the vector set is externally defined.</li>
 * </ul>
 *
 * @param <S>
 */
public class RVEABuilder<S extends Solution<?>> {
  private static final double MINIMUM_FREQUENCY_RATIO = 1.0e-64;

  private final String name;
  private final Problem<S> problem;
  private final int populationSize;
  private final double alpha;
  private final double fr;
  private List<double[]> referenceVectors;
  private Evaluation<S> evaluation;
  private SolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private Selection<S> selection;
  private Variation<S> variation;
  private Replacement<S> replacement;
  private boolean customSelection;

  /**
   * Creates a builder for the RVEA algorithm using reference vectors generated from
   * the number of objectives and {@code h}.
   *
   * <p>Parameter constraints:
   * <ul>
   *   <li>{@code problem}, {@code crossover}, and {@code mutation} must be non-null.</li>
   *   <li>{@code populationSize}, {@code maxEvaluations}, and {@code h} must be greater than 0.</li>
   *   <li>{@code alpha} must be greater than or equal to 0.</li>
   *   <li>{@code fr} must be in [{@value #MINIMUM_FREQUENCY_RATIO}, 1.0].</li>
   * </ul>
   *
   * <p>RVEA requires termination by evaluations in {@link #build()} to compute APD progress and
   * reference adaptation.
   */
  public RVEABuilder(Problem<S> problem, int populationSize, int maxEvaluations,
      CrossoverOperator<S> crossover, MutationOperator<S> mutation, double alpha, double fr, int h) {
    this(problem, populationSize, maxEvaluations, crossover, mutation, alpha, fr,
        ReferencePointGenerator.generateSingleLayer(
            validateProblem(problem).numberOfObjectives(), validateNumberOfDivisions(h)));
  }

  /**
   * Creates a builder for the RVEA algorithm using caller-provided reference vectors.
   *
   * <p>Parameter constraints:
   * <ul>
   *   <li>{@code problem}, {@code crossover}, {@code mutation}, and {@code referenceVectors} must be non-null.</li>
   *   <li>{@code referenceVectors} must not be empty and must not contain null entries.</li>
   *   <li>{@code populationSize}, {@code maxEvaluations} must be greater than 0.</li>
   *   <li>{@code alpha} must be greater than or equal to 0.</li>
   *   <li>{@code fr} must be in [{@value #MINIMUM_FREQUENCY_RATIO}, 1.0].</li>
   *   <li>The number of vectors must match {@code populationSize}.</li>
   * </ul>
   *
   * <p>RVEA requires termination by evaluations in {@link #build()} to compute APD progress and
   * reference adaptation.
   */
  public RVEABuilder(Problem<S> problem, int populationSize, int maxEvaluations,
      CrossoverOperator<S> crossover, MutationOperator<S> mutation, double alpha, double fr,
      List<double[]> referenceVectors) {
    Check.notNull(problem, "problem");
    Check.notNull(crossover, "crossover");
    Check.notNull(mutation, "mutation");
    Check.notNull(referenceVectors, "referenceVectors");
    Check.notNullAndNotEmpty(referenceVectors, "referenceVectors");
    Check.noNullElements(referenceVectors, "referenceVectors");
    Check.valueIsPositive(populationSize, "populationSize");
    Check.valueIsPositive(maxEvaluations, "maxEvaluations");
    Check.valueIsNotNegative(alpha, "alpha");
    Check.valueIsInRange(fr, MINIMUM_FREQUENCY_RATIO, 1.0, "fr");
    Check.that(referenceVectors.size() == populationSize,
      "Population size must match the number of generated reference vectors. Expected "
        + referenceVectors.size() + " and found " + populationSize + ".");

    int numberOfObjectives = problem.numberOfObjectives();
    for (double[] referenceVector : referenceVectors) {
      Check.that(referenceVector.length == numberOfObjectives,
        "Reference vector dimension " + referenceVector.length
          + " does not match the number of objectives " + numberOfObjectives + ".");
    }

    this.name = "RVEA";
    this.problem = problem;
    this.populationSize = populationSize;
    this.alpha = alpha;
    this.fr = fr;
    this.referenceVectors = cloneReferenceVectors(referenceVectors);
    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    this.variation = new CrossoverAndMutationVariation<>(
        populationSize, crossover, mutation);

    this.selection = new RandomSelection<>(variation.matingPoolSize());
    this.customSelection = false;

    this.termination = new TerminationByEvaluations(maxEvaluations);
    this.evaluation = new SequentialEvaluation<>(problem);
  }

  private static <S extends Solution<?>> Problem<S> validateProblem(Problem<S> problem) {
    Check.notNull(problem, "problem");
    return problem;
  }

  private static int validateNumberOfDivisions(int numberOfDivisions) {
    Check.valueIsPositive(numberOfDivisions, "numberOfDivisions");
    return numberOfDivisions;
  }

  private List<double[]> cloneReferenceVectors(List<double[]> referenceVectors) {
    List<double[]> clonedReferenceVectors = new ArrayList<>(referenceVectors.size());
    for (double[] referenceVector : referenceVectors) {
      clonedReferenceVectors.add(referenceVector.clone());
    }

    return clonedReferenceVectors;
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

  public EvolutionaryAlgorithm<S> build() {
    Check.that(termination instanceof TerminationByEvaluations,
        "RVEA requires termination by evaluations to compute APD progress and reference adaptation.");

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
