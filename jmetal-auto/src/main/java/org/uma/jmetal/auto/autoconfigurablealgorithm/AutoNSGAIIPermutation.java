package org.uma.jmetal.auto.autoconfigurablealgorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalIntegerParameter;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.auto.parameter.IntegerParameter;
import org.uma.jmetal.auto.parameter.Parameter;
import org.uma.jmetal.auto.parameter.PositiveIntegerValue;
import org.uma.jmetal.auto.parameter.RealParameter;
import org.uma.jmetal.auto.parameter.StringParameter;
import org.uma.jmetal.auto.parameter.catalogue.*;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluationWithArchive;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.util.RankingAndDensityEstimatorPreference;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/**
 * Class to configure NSGA-II with an argument string using class {@link EvolutionaryAlgorithm}
 *
 * @autor Antonio J. Nebro
 */
public class AutoNSGAIIPermutation implements AutoConfigurableAlgorithm {
  private List<Parameter<?>> configurableParameterList = new ArrayList<>();
  private List<Parameter<?>> fixedParameterList = new ArrayList<>();
  private StringParameter problemNameParameter;
  public StringParameter referenceFrontFilename;
  private PositiveIntegerValue randomGeneratorSeedParameter;
  private PositiveIntegerValue maximumNumberOfEvaluationsParameter;
  private CategoricalParameter algorithmResultParameter;
  private ExternalArchiveParameter<PermutationSolution<Integer>> externalArchiveParameter;
  private PositiveIntegerValue populationSizeParameter;
  private IntegerParameter populationSizeWithArchiveParameter;
  private CategoricalIntegerParameter offspringPopulationSizeParameter;
  private CreateInitialSolutionsParameter createInitialSolutionsParameter;
  private SelectionParameter<PermutationSolution<Integer>> selectionParameter;
  private VariationParameter variationParameter;

  @Override
  public List<Parameter<?>> configurableParameterList() {
    return configurableParameterList;
  }
  @Override
  public List<Parameter<?>> fixedParameterList() {
    return fixedParameterList;
  }

  public AutoNSGAIIPermutation() {
    this.configure();
  }

  private void configure() {
    problemNameParameter = new StringParameter("problemName");
    randomGeneratorSeedParameter = new PositiveIntegerValue("randomGeneratorSeed");
    referenceFrontFilename = new StringParameter("referenceFrontFileName");
    maximumNumberOfEvaluationsParameter =
        new PositiveIntegerValue("maximumNumberOfEvaluations");
    populationSizeParameter = new PositiveIntegerValue("populationSize");

    fixedParameterList.add(populationSizeParameter);
    fixedParameterList.add(problemNameParameter);
    fixedParameterList.add(referenceFrontFilename);
    fixedParameterList.add(maximumNumberOfEvaluationsParameter);
    fixedParameterList.add(randomGeneratorSeedParameter);


    algorithmResult();
    createInitialSolution();
    selection();
    variation();

    configurableParameterList.add(algorithmResultParameter);
    configurableParameterList.add(createInitialSolutionsParameter);
    configurableParameterList.add(variationParameter);
    configurableParameterList.add(selectionParameter);
  }

  private void variation() {
    CrossoverParameter crossoverParameter = new CrossoverParameter(
        List.of("PMX"));
    ProbabilityParameter crossoverProbability =
        new ProbabilityParameter("crossoverProbability");
    crossoverParameter.addGlobalParameter(crossoverProbability);

    MutationParameter mutationParameter =
        new MutationParameter(List.of("swap"));

    RealParameter mutationProbabilityFactor = new RealParameter("mutationProbabilityFactor",
        0.0, 2.0);
    mutationParameter.addGlobalParameter(mutationProbabilityFactor);

    offspringPopulationSizeParameter = new CategoricalIntegerParameter("offspringPopulationSize",
        List.of(1, 2, 5, 10, 20, 50, 100, 150, 200, 300, 400));

    variationParameter =
        new VariationParameter(List.of("crossoverAndMutationVariation"));
    variationParameter.addSpecificParameter("crossoverAndMutationVariation",
        offspringPopulationSizeParameter);
    variationParameter.addSpecificParameter("crossoverAndMutationVariation", crossoverParameter);
    variationParameter.addSpecificParameter("crossoverAndMutationVariation", mutationParameter);
  }

  private void selection() {
    selectionParameter = new SelectionParameter<>(Arrays.asList("tournament", "random"));
    IntegerParameter selectionTournamentSize =
        new IntegerParameter("selectionTournamentSize", 2, 10);
    selectionParameter.addSpecificParameter("tournament", selectionTournamentSize);
  }

  private void createInitialSolution() {
    createInitialSolutionsParameter =
        new CreateInitialSolutionsParameter(List.of("random"));
  }

  private void algorithmResult() {
    algorithmResultParameter =
        new CategoricalParameter("algorithmResult", List.of("externalArchive", "population"));
    populationSizeWithArchiveParameter = new IntegerParameter("populationSizeWithArchive", 10,
        200);
    externalArchiveParameter = new ExternalArchiveParameter(
        List.of("crowdingDistanceArchive", "unboundedArchive"));
    algorithmResultParameter.addSpecificParameter(
        "externalArchive", populationSizeWithArchiveParameter);

    algorithmResultParameter.addSpecificParameter(
        "externalArchive", externalArchiveParameter);
  }

  @Override
  public void parse(String[] arguments) {
    for (Parameter<?> parameter : fixedParameterList) {
      parameter.parse(arguments).check();
    }
    for (Parameter<?> parameter : configurableParameterList()) {
      parameter.parse(arguments).check();
    }
  }


  protected Problem<PermutationSolution<Integer>> problem() {
    return ProblemFactory.loadProblem(problemNameParameter.value());
  }

  /**
   * Creates an instance of NSGA-II from the parsed parameters
   *
   * @return
   */
  public EvolutionaryAlgorithm<PermutationSolution<Integer>> create() throws IOException {
    JMetalRandom.getInstance().setSeed(randomGeneratorSeedParameter.value());

    var problem = (PermutationProblem<PermutationSolution<Integer>>)problem();

    Archive<PermutationSolution<Integer>> archive = null;

    if (algorithmResultParameter.value().equals("externalArchive")) {
      externalArchiveParameter.setSize(populationSizeParameter.value());
      archive = externalArchiveParameter.getParameter();
      populationSizeParameter.value(populationSizeWithArchiveParameter.value());
    }

    Ranking<PermutationSolution<Integer>> ranking = new FastNonDominatedSortRanking<>(
        new DominanceWithConstraintsComparator<>());
    DensityEstimator<PermutationSolution<Integer>> densityEstimator = new CrowdingDistanceDensityEstimator<>();
    MultiComparator<PermutationSolution<Integer>> rankingAndCrowdingComparator =
        new MultiComparator<>(
            Arrays.asList(
                Comparator.comparing(ranking::getRank),
                Comparator.comparing(densityEstimator::value).reversed()));

    var initialSolutionsCreation =
        (SolutionsCreation<PermutationSolution<Integer>>) createInitialSolutionsParameter.getParameter(
            problem,
            populationSizeParameter.value());

    MutationParameter mutationParameter = (MutationParameter) variationParameter.findSpecificParameter(
        "mutation");
    mutationParameter.addNonConfigurableParameter("permutationLength",
        problem.numberOfVariables());

    var variation = (Variation<PermutationSolution<Integer>>) variationParameter.getPermutationSolutionParameter();

    Selection<PermutationSolution<Integer>> selection =
        selectionParameter.getParameter(
            variation.getMatingPoolSize(), rankingAndCrowdingComparator);

    Evaluation<PermutationSolution<Integer>> evaluation;
    if (algorithmResultParameter.value().equals("externalArchive")) {
      evaluation = new SequentialEvaluationWithArchive<>(problem, archive);
    } else {
      evaluation = new SequentialEvaluation<>(problem);
    }

    RankingAndDensityEstimatorPreference<PermutationSolution<Integer>> preferenceForReplacement = new RankingAndDensityEstimatorPreference<>(
        ranking, densityEstimator);
    Replacement<PermutationSolution<Integer>> replacement =
        new RankingAndDensityEstimatorReplacement<>(preferenceForReplacement,
            Replacement.RemovalPolicy.ONE_SHOT);

    Termination termination =
        new TerminationByEvaluations(maximumNumberOfEvaluationsParameter.value());

    class EvolutionaryAlgorithmWithArchive extends EvolutionaryAlgorithm<PermutationSolution<Integer>> {

      private Archive<PermutationSolution<Integer>> archive;

      /**
       * Constructor
       *
       * @param name                      Algorithm name
       * @param initialPopulationCreation
       * @param evaluation
       * @param termination
       * @param selection
       * @param variation
       * @param replacement
       */
      public EvolutionaryAlgorithmWithArchive(String name,
          SolutionsCreation<PermutationSolution<Integer>> initialPopulationCreation,
          Evaluation<PermutationSolution<Integer>> evaluation, Termination termination,
          Selection<PermutationSolution<Integer>> selection, Variation<PermutationSolution<Integer>> variation,
          Replacement<PermutationSolution<Integer>> replacement,
          Archive<PermutationSolution<Integer>> archive) {
        super(name, initialPopulationCreation, evaluation, termination, selection, variation,
            replacement);
        this.archive = archive;
      }

      @Override
      public List<PermutationSolution<Integer>> result() {
        return archive.solutions() ;
      }
    }

    if (algorithmResultParameter.value().equals("externalArchive")) {
      return new EvolutionaryAlgorithmWithArchive(
          "NSGA-II",
          initialSolutionsCreation,
          evaluation,
          termination,
          selection,
          variation,
          replacement,
          archive);
    } else {
      return new EvolutionaryAlgorithm<>(
          "NSGA-II",
          initialSolutionsCreation,
          evaluation,
          termination,
          selection,
          variation,
          replacement);
    }
  }

  public static void print(List<Parameter<?>> parameterList) {
    parameterList.forEach(System.out::println);
  }
}
