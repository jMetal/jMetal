package org.uma.jmetal.component.algorithm.multiobjective;

import java.util.List;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RVEAReplacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea.IRVEAEnvironmentalSelection;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea.RVEAEnvironmentalSelection;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea.RVEAStarEnvironmentalSelection;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.RandomSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.referencepoint.ReferencePointGenerator;

/**
 * Builds component-based RVEA algorithms for unconstrained minimization.
 *
 * <p>The initial population and each offspring batch contain N solutions, where N is the number of
 * predefined vectors. Survivor populations can vary in size. Termination must be by evaluations to
 * define the APD schedule; the final offspring batch may exceed the budget by fewer than N
 * evaluations. Each call to {@link #build()} creates fresh vector and archive state.
 *
 * @param <S> solution type
 */
public class RVEABuilder<S extends Solution<?>> {
  protected enum Variant {
    RVEA,
    RVEA_STAR,
    IRVEA
  }

  private final Variant variant;
  private final Problem<S> problem;
  private final int populationSize;
  private final double alpha;
  private final double fr;
  private final List<double[]> referenceVectors;
  private Evaluation<S> evaluation;
  private SolutionsCreation<S> initialPopulation;
  private Termination termination;
  private Selection<S> selection;
  private Variation<S> variation;
  private boolean customSelection;
  protected int numberOfSubregions = 40;

  public RVEABuilder(
      Problem<S> problem,
      int populationSize,
      int maxEvaluations,
      CrossoverOperator<S> crossover,
      MutationOperator<S> mutation,
      double alpha,
      double fr,
      int divisions) {
    this(
        problem,
        populationSize,
        maxEvaluations,
        crossover,
        mutation,
        alpha,
        fr,
        generateVectors(problem, divisions));
  }

  public RVEABuilder(
      Problem<S> problem,
      int populationSize,
      int maxEvaluations,
      CrossoverOperator<S> crossover,
      MutationOperator<S> mutation,
      double alpha,
      double fr,
      List<double[]> vectors) {
    this(
        Variant.RVEA,
        problem,
        populationSize,
        maxEvaluations,
        crossover,
        mutation,
        alpha,
        fr,
        vectors);
  }

  protected RVEABuilder(
      Variant variant,
      Problem<S> problem,
      int populationSize,
      int maxEvaluations,
      CrossoverOperator<S> crossover,
      MutationOperator<S> mutation,
      double alpha,
      double fr,
      List<double[]> vectors) {
    Check.notNull(problem);
    Check.notNull(crossover);
    Check.notNull(mutation);
    Check.that(problem.numberOfConstraints() == 0, "RVEA requires an unconstrained problem");
    Check.valueIsPositive(populationSize, "populationSize");
    Check.valueIsPositive(maxEvaluations, "maxEvaluations");
    Check.valueIsNotNegative(alpha, "alpha");
    Check.valueIsInRange(fr, 1.0e-64, 1.0, "fr");
    Check.that(maxEvaluations >= populationSize, "The budget must cover the initial population");
    var validation =
        new RVEAEnvironmentalSelection<S>(problem.numberOfObjectives(), 1, alpha, fr, vectors);
    Check.that(
        vectors.size() == populationSize,
        "Population size must match the number of reference vectors");
    this.referenceVectors = List.of(validation.referenceVectors());
    this.variant = variant;
    this.problem = problem;
    this.populationSize = populationSize;
    this.alpha = alpha;
    this.fr = fr;
    this.evaluation = new SequentialEvaluation<>(problem);
    this.initialPopulation = new RandomSolutionsCreation<>(problem, populationSize);
    this.termination = new TerminationByEvaluations(maxEvaluations);
    this.variation = new CrossoverAndMutationVariation<>(populationSize, crossover, mutation);
    this.selection = new RandomSelection<>(variation.matingPoolSize());
  }

  protected static List<double[]> generateVectors(Problem<?> problem, int divisions) {
    Check.notNull(problem);
    Check.valueIsPositive(divisions, "divisions");
    return ReferencePointGenerator.generateSingleLayer(problem.numberOfObjectives(), divisions);
  }

  public RVEABuilder<S> setTermination(Termination termination) {
    Check.notNull(termination);
    this.termination = termination;
    return this;
  }

  public RVEABuilder<S> setEvaluation(Evaluation<S> evaluation) {
    Check.notNull(evaluation);
    this.evaluation = evaluation;
    return this;
  }

  public RVEABuilder<S> setCreateInitialPopulation(SolutionsCreation<S> creation) {
    Check.notNull(creation);
    this.initialPopulation = creation;
    return this;
  }

  public RVEABuilder<S> setSelection(Selection<S> selection) {
    Check.notNull(selection);
    this.selection = selection;
    this.customSelection = true;
    return this;
  }

  public RVEABuilder<S> setVariation(Variation<S> variation) {
    Check.notNull(variation);
    this.variation = variation;
    return this;
  }

  public EvolutionaryAlgorithm<S> build() {
    if (!(termination instanceof TerminationByEvaluations byEvaluations)) {
      throw new InvalidConditionException("RVEA requires termination by evaluations");
    }
    int budget = byEvaluations.getMaximumNumberOfEvaluations();
    int offspringSize = variation.offspringPopulationSize();
    Check.that(budget >= populationSize, "The budget must cover the initial population");
    Check.that(offspringSize > 0, "The offspring population size must be positive");
    Check.that(variation.matingPoolSize() > 0, "The mating pool size must be positive");
    int generations =
        Math.max(1, (int) Math.ceil((double) (budget - populationSize) / offspringSize));
    RVEAEnvironmentalSelection<S> environmentalSelection =
        switch (variant) {
          case RVEA ->
              new RVEAEnvironmentalSelection<>(
                  problem.numberOfObjectives(), generations, alpha, fr, referenceVectors);
          case RVEA_STAR ->
              new RVEAStarEnvironmentalSelection<>(
                  problem.numberOfObjectives(), generations, alpha, fr, referenceVectors);
          case IRVEA ->
              new IRVEAEnvironmentalSelection<>(
                  problem.numberOfObjectives(),
                  generations,
                  alpha,
                  fr,
                  referenceVectors,
                  numberOfSubregions);
        };
    String name =
        switch (variant) {
          case RVEA -> "RVEA";
          case RVEA_STAR -> "RVEA*";
          case IRVEA -> "iRVEA";
        };
    // Capture the components now, so subsequent builder changes cannot alter this algorithm.
    SolutionsCreation<S> creation = initialPopulation;
    SolutionsCreation<S> validatedCreation =
        () -> {
          List<S> population = creation.create();
          Check.notNull(population);
          Check.that(
              population.size() == populationSize,
              "The initial population must contain N solutions");
          return population;
        };
    Selection<S> matingSelection =
        customSelection ? selection : new RandomSelection<>(variation.matingPoolSize());
    return new EvolutionaryAlgorithm<>(
        name,
        validatedCreation,
        evaluation,
        termination,
        matingSelection,
        variation,
        new RVEAReplacement<>(environmentalSelection));
  }
}
