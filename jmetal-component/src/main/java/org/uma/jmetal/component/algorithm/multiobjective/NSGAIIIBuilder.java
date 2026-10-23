package org.uma.jmetal.component.algorithm.multiobjective;

import java.util.Arrays;
import java.util.Comparator;
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
import org.uma.jmetal.component.catalogue.ea.replacement.impl.NSGAIIIReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.component.util.RunProgress;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.constrainthandling.ConstraintHandlingTechnique;
import org.uma.jmetal.util.densityestimator.impl.ReferencePointNicheDistanceEstimator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.referencepoint.ReferencePointGenerator;

/**
 * Builder class for NSGA-III algorithm using the component-based architecture.
 *
 * <p>
 * NSGA-III uses reference points to maintain diversity in many-objective
 * optimization.
 * The population size is automatically determined from the number of reference
 * points.
 *
 * <p>
 * Reference: K. Deb and H. Jain, "An Evolutionary Many-Objective Optimization
 * Algorithm
 * Using Reference-Point-Based Nondominated Sorting Approach, Part I: Solving
 * Problems
 * With Box Constraints," IEEE TEVC, vol. 18, no. 4, pp. 577-601, Aug. 2014.
 *
 * @author Antonio J. Nebro
 * @param <S> Type of solution
 */
public class NSGAIIIBuilder<S extends Solution<?>> {

  private final String name;
  private final Problem<S> problem;
  private final List<double[]> referencePoints;
  private final int populationSize;

  private Ranking<S> ranking;
  private ReferencePointNicheDistanceEstimator<S> densityEstimator;
  private Evaluation<S> evaluation;
  private SolutionsCreation<S> createInitialPopulation;
  private Termination termination;
  private Selection<S> selection;
  private Variation<S> variation;
  private ConstraintHandlingTechnique<S> constraintHandlingTechnique;

  /**
   * Constructor with single-layer reference points.
   *
   * @param problem           The optimization problem
   * @param numberOfDivisions Number of divisions for reference point generation
   * @param crossover         Crossover operator
   * @param mutation          Mutation operator
   */
  public NSGAIIIBuilder(
      Problem<S> problem,
      int numberOfDivisions,
      CrossoverOperator<S> crossover,
      MutationOperator<S> mutation) {
    this(problem,
        ReferencePointGenerator.generateSingleLayer(problem.numberOfObjectives(), numberOfDivisions),
        crossover,
        mutation);
  }

  /**
   * Constructor with two-layer reference points for many-objective problems.
   *
   * @param problem        The optimization problem
   * @param outerDivisions Divisions for outer layer
   * @param innerDivisions Divisions for inner layer
   * @param crossover      Crossover operator
   * @param mutation       Mutation operator
   */
  public NSGAIIIBuilder(
      Problem<S> problem,
      int outerDivisions,
      int innerDivisions,
      CrossoverOperator<S> crossover,
      MutationOperator<S> mutation) {
    this(problem,
        ReferencePointGenerator.generateTwoLayers(problem.numberOfObjectives(), outerDivisions, innerDivisions),
        crossover,
        mutation);
  }

  /**
   * Constructor with custom reference points.
   *
   * @param problem         The optimization problem
   * @param referencePoints Custom reference points
   * @param crossover       Crossover operator
   * @param mutation        Mutation operator
   */
  public NSGAIIIBuilder(
      Problem<S> problem,
      List<double[]> referencePoints,
      CrossoverOperator<S> crossover,
      MutationOperator<S> mutation) {

    this.name = "NSGAIII";
    this.problem = problem;
    this.referencePoints = referencePoints;

    // Population size from reference points, rounded up to multiple of 4
    int refPointCount = referencePoints.size();
    this.populationSize = refPointCount + (4 - refPointCount % 4) % 4;

    // Initialize components
    this.ranking = new FastNonDominatedSortRanking<>();

    this.densityEstimator = new ReferencePointNicheDistanceEstimator<>(
        referencePoints, problem.numberOfObjectives());

    this.createInitialPopulation = new RandomSolutionsCreation<>(problem, populationSize);

    int offspringPopulationSize = populationSize;
    this.variation = new CrossoverAndMutationVariation<>(offspringPopulationSize, crossover, mutation);

    this.termination = new TerminationByEvaluations(25000);
    this.evaluation = new SequentialEvaluation<>(problem);
  }

  public NSGAIIIBuilder<S> setTermination(Termination termination) {
    this.termination = termination;
    return this;
  }

  public NSGAIIIBuilder<S> setEvaluation(Evaluation<S> evaluation) {
    this.evaluation = evaluation;
    return this;
  }

  public NSGAIIIBuilder<S> setCreateInitialPopulation(SolutionsCreation<S> solutionsCreation) {
    this.createInitialPopulation = solutionsCreation;
    return this;
  }

  /** Sets the ranking of the replacement and of the default selection. */
  public NSGAIIIBuilder<S> setRanking(Ranking<S> ranking) {
    this.ranking = ranking;
    return this;
  }

  /**
   * Sets the constraint-handling technique: the ranking of the replacement and of the default
   * selection becomes {@code technique.ranking()} (it replaces a ranking given with {@code
   * setRanking}), and the technique is updated once per generation with the progress of the run.
   * Without a technique the algorithm behaves as before (its default ranking ignores the
   * constraints). The technique only guides the search: an external archive keeps the feasible
   * solutions with the constrained-domination principle, which is recommended with {@link
   * org.uma.jmetal.util.constrainthandling.StochasticRanking} and {@link
   * org.uma.jmetal.util.constrainthandling.SelfAdaptivePenalty}, whose final population can contain
   * infeasible solutions. A technique has state, so it must not be shared by several algorithms.
   */
  public NSGAIIIBuilder<S> setConstraintHandlingTechnique(
      ConstraintHandlingTechnique<S> technique) {
    this.constraintHandlingTechnique = technique;
    return this;
  }

  /** Sets the selection, which replaces the default binary tournament. */
  public NSGAIIIBuilder<S> setSelection(Selection<S> selection) {
    this.selection = selection;
    return this;
  }

  public NSGAIIIBuilder<S> setVariation(Variation<S> variation) {
    this.variation = variation;
    return this;
  }

  /**
   * Returns the computed population size (based on reference points).
   */
  public int getPopulationSize() {
    return populationSize;
  }

  /**
   * Returns the reference points.
   */
  public List<double[]> getReferencePoints() {
    return referencePoints;
  }

  /**
   * Builds and returns the NSGA-III algorithm instance.
   */
  public EvolutionaryAlgorithm<S> build() {
    Ranking<S> algorithmRanking =
        constraintHandlingTechnique != null ? constraintHandlingTechnique.ranking() : ranking;
    RunProgress progress = new RunProgress();
    Replacement<S> replacement = new NSGAIIIReplacement<>(
        algorithmRanking, referencePoints, problem.numberOfObjectives(), populationSize);
    if (constraintHandlingTechnique != null) {
      replacement =
          new ConstraintHandlingReplacement<>(constraintHandlingTechnique, replacement, progress);
    }
    Selection<S> algorithmSelection =
        selection != null ? selection : defaultSelection(algorithmRanking);

    var algorithm = new EvolutionaryAlgorithm<>(
        name,
        createInitialPopulation,
        evaluation,
        termination,
        algorithmSelection,
        variation,
        replacement);
    progress.bind(() -> algorithm.termination(), algorithm::attributes);

    return algorithm;
  }

  /** A binary tournament that compares the ranks and then the distances to the reference points. */
  private Selection<S> defaultSelection(Ranking<S> ranking) {
    int tournamentSize = 2;
    return new NaryTournamentSelection<>(
        tournamentSize,
        variation.matingPoolSize(),
        new MultiComparator<>(
            Arrays.asList(
                Comparator.comparing(ranking::getRank),
                Comparator.comparing(densityEstimator::value))));
  }
}
