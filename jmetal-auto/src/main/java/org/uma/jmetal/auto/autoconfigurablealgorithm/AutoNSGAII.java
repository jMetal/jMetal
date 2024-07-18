package org.uma.jmetal.auto.autoconfigurablealgorithm;

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
import org.uma.jmetal.auto.parameter.catalogue.CreateInitialSolutionsParameter;
import org.uma.jmetal.auto.parameter.catalogue.CrossoverParameter;
import org.uma.jmetal.auto.parameter.catalogue.ExternalArchiveParameter;
import org.uma.jmetal.auto.parameter.catalogue.MutationParameter;
import org.uma.jmetal.auto.parameter.catalogue.ProbabilityParameter;
import org.uma.jmetal.auto.parameter.catalogue.RepairDoubleSolutionStrategyParameter;
import org.uma.jmetal.auto.parameter.catalogue.SelectionParameter;
import org.uma.jmetal.auto.parameter.catalogue.VariationParameter;
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
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
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
public class AutoNSGAII implements AutoConfigurableAlgorithm {
  private List<Parameter<?>> configurableParameterList = new ArrayList<>();
  private List<Parameter<?>> fixedParameterList = new ArrayList<>();
  private StringParameter problemNameParameter;
  public StringParameter referenceFrontFilename;
  private PositiveIntegerValue randomGeneratorSeedParameter;
  private PositiveIntegerValue maximumNumberOfEvaluationsParameter;
  private CategoricalParameter algorithmResultParameter;
  private ExternalArchiveParameter<DoubleSolution> externalArchiveParameter;
  private PositiveIntegerValue populationSizeParameter;
  private CategoricalIntegerParameter populationSizeWithArchiveParameter;
  //private IntegerParameter offspringPopulationSizeParameter;
  private CategoricalIntegerParameter offspringPopulationSizeParameter;
  private CreateInitialSolutionsParameter createInitialSolutionsParameter;
  private SelectionParameter<DoubleSolution> selectionParameter;
  private VariationParameter variationParameter;

  @Override
  public List<Parameter<?>> configurableParameterList() {
    return configurableParameterList;
  }
  @Override
  public List<Parameter<?>> fixedParameterList() {
    return fixedParameterList;
  }

  public AutoNSGAII() {
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
        List.of("SBX", "BLX_ALPHA", "wholeArithmetic"));
    ProbabilityParameter crossoverProbability =
        new ProbabilityParameter("crossoverProbability");
    crossoverParameter.addGlobalParameter(crossoverProbability);
    RepairDoubleSolutionStrategyParameter crossoverRepairStrategy =
        new RepairDoubleSolutionStrategyParameter(
            "crossoverRepairStrategy", Arrays.asList("random", "round", "bounds"));
    crossoverParameter.addGlobalParameter(crossoverRepairStrategy);

    RealParameter distributionIndex = new RealParameter("sbxDistributionIndex", 5.0, 400.0);
    crossoverParameter.addSpecificParameter("SBX", distributionIndex);

    RealParameter alpha = new RealParameter("blxAlphaCrossoverAlphaValue", 0.0, 1.0);
    crossoverParameter.addSpecificParameter("BLX_ALPHA", alpha);

    MutationParameter mutationParameter =
        new MutationParameter(
            Arrays.asList("uniform", "polynomial", "linkedPolynomial", "nonUniform"));

    RealParameter mutationProbabilityFactor = new RealParameter("mutationProbabilityFactor",
        0.0, 2.0);
    mutationParameter.addGlobalParameter(mutationProbabilityFactor);
    RepairDoubleSolutionStrategyParameter mutationRepairStrategy =
        new RepairDoubleSolutionStrategyParameter(
            "mutationRepairStrategy", Arrays.asList("random", "round", "bounds"));
    mutationParameter.addGlobalParameter(mutationRepairStrategy);

    RealParameter distributionIndexForPolynomialMutation =
        new RealParameter("polynomialMutationDistributionIndex", 5.0, 400.0);
    mutationParameter.addSpecificParameter("polynomial", distributionIndexForPolynomialMutation);

    RealParameter distributionIndexForLinkedPolynomialMutation =
        new RealParameter("linkedPolynomialMutationDistributionIndex", 5.0, 400.0);
    mutationParameter.addSpecificParameter("linkedPolynomial",
        distributionIndexForLinkedPolynomialMutation);

    RealParameter uniformMutationPerturbation =
        new RealParameter("uniformMutationPerturbation", 0.0, 1.0);
    mutationParameter.addSpecificParameter("uniform", uniformMutationPerturbation);

    RealParameter nonUniformMutationPerturbation =
        new RealParameter("nonUniformMutationPerturbation", 0.0, 1.0);
    mutationParameter.addSpecificParameter("nonUniform", nonUniformMutationPerturbation);

    //offspringPopulationSizeParameter = new IntegerParameter("offspringPopulationSize", 1,  400);
    offspringPopulationSizeParameter = new CategoricalIntegerParameter("offspringPopulationSize",
        List.of(1, 2, 5, 10, 20, 50, 100, 150, 200, 300, 400));
    offspringPopulationSizeParameter = new CategoricalIntegerParameter("offspringPopulationSize",
        List.of(100));

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
        new CreateInitialSolutionsParameter(
            Arrays.asList("random", "latinHypercubeSampling", "scatterSearch"));
  }

  private void algorithmResult() {
    algorithmResultParameter =
        new CategoricalParameter("algorithmResult", List.of("externalArchive", "population"));
    populationSizeWithArchiveParameter = new CategoricalIntegerParameter("populationSizeWithArchive", List.of(100));
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


  protected Problem<DoubleSolution> problem() {
    return ProblemFactory.loadProblem(problemNameParameter.value());
  }

  /**
   * Creates an instance of NSGA-II from the parsed parameters
   *
   * @return
   */
  public EvolutionaryAlgorithm<DoubleSolution> create() {
    //JMetalRandom.getInstance().setSeed(randomGeneratorSeedParameter.value());

    System.out.println(problemNameParameter.value()) ;
    Problem<DoubleSolution> problem = ProblemFactory.loadProblem(problemNameParameter.value());

    Archive<DoubleSolution> archive = null;

    if (algorithmResultParameter.value().equals("externalArchive")) {
      externalArchiveParameter.setSize(populationSizeParameter.value());
      archive = externalArchiveParameter.getParameter();
      populationSizeParameter.value(populationSizeWithArchiveParameter.value());
    }

    Ranking<DoubleSolution> ranking = new FastNonDominatedSortRanking<>(
        new DominanceWithConstraintsComparator<>());
    DensityEstimator<DoubleSolution> densityEstimator = new CrowdingDistanceDensityEstimator<>();
    MultiComparator<DoubleSolution> rankingAndCrowdingComparator =
        new MultiComparator<>(
            Arrays.asList(
                Comparator.comparing(ranking::getRank),
                Comparator.comparing(densityEstimator::value).reversed()));

    var initialSolutionsCreation =
        (SolutionsCreation<DoubleSolution>) createInitialSolutionsParameter.getParameter(
            (DoubleProblem) problem,
            populationSizeParameter.value());

    MutationParameter mutationParameter = (MutationParameter) variationParameter.findSpecificParameter(
        "mutation");
    mutationParameter.addNonConfigurableParameter("numberOfProblemVariables",
        problem.numberOfVariables());

    if (mutationParameter.value().equals("nonUniform")) {
      mutationParameter.addSpecificParameter("nonUniform", maximumNumberOfEvaluationsParameter);
      mutationParameter.addNonConfigurableParameter("maxIterations",
          maximumNumberOfEvaluationsParameter.value() / populationSizeParameter.value());
    }

    var variation = (Variation<DoubleSolution>) variationParameter.getDoubleSolutionParameter();

    Selection<DoubleSolution> selection =
        selectionParameter.getParameter(
            variation.getMatingPoolSize(), rankingAndCrowdingComparator);

    Evaluation<DoubleSolution> evaluation;
    if (algorithmResultParameter.value().equals("externalArchive")) {
      evaluation = new SequentialEvaluationWithArchive<>(problem, archive);
    } else {
      evaluation = new SequentialEvaluation<>(problem);
    }

    RankingAndDensityEstimatorPreference<DoubleSolution> preferenceForReplacement = new RankingAndDensityEstimatorPreference<>(
        ranking, densityEstimator);
    Replacement<DoubleSolution> replacement =
        new RankingAndDensityEstimatorReplacement<>(preferenceForReplacement,
            Replacement.RemovalPolicy.ONE_SHOT);

    Termination termination =
        new TerminationByEvaluations(maximumNumberOfEvaluationsParameter.value());

    class EvolutionaryAlgorithmWithArchive extends EvolutionaryAlgorithm<DoubleSolution> {

      private Archive<DoubleSolution> archive;

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
          SolutionsCreation<DoubleSolution> initialPopulationCreation,
          Evaluation<DoubleSolution> evaluation, Termination termination,
          Selection<DoubleSolution> selection, Variation<DoubleSolution> variation,
          Replacement<DoubleSolution> replacement,
          Archive<DoubleSolution> archive) {
        super(name, initialPopulationCreation, evaluation, termination, selection, variation,
            replacement);
        this.archive = archive;
      }

      @Override
      public List<DoubleSolution> result() {
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
