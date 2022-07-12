package org.uma.jmetal.auto.autoconfigurablealgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
import org.uma.jmetal.component.catalogue.ea.selection.MatingPoolSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.util.RankingAndDensityEstimatorPreference;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/**
 * Class to configure NSGA-II with an argument string using class {@link EvolutionaryAlgorithm}
 *
 * @autor Antonio J. Nebro
 */
public class AutoNSGAII implements AutoConfigurableAlgorithm {

  public List<Parameter<?>> autoConfigurableParameterList = new ArrayList<>();
  public List<Parameter<?>> fixedParameterList = new ArrayList<>();
  private StringParameter problemNameParameter;
  public StringParameter referenceFrontFilename;
  private PositiveIntegerValue maximumNumberOfEvaluationsParameter;
  private CategoricalParameter algorithmResultParameter;
  private ExternalArchiveParameter<DoubleSolution> externalArchiveParameter;
  private PositiveIntegerValue populationSizeParameter;
  private IntegerParameter populationSizeWithArchiveParameter;
  private IntegerParameter offspringPopulationSizeParameter;
  private CreateInitialSolutionsParameter createInitialSolutionsParameter;
  private SelectionParameter<DoubleSolution> selectionParameter;
  private VariationParameter variationParameter;

  @Override
  public List<Parameter<?>> getAutoConfigurableParameterList() {
    return autoConfigurableParameterList;
  }

  @Override
  public void parseAndCheckParameters(String[] args) {
    problemNameParameter = new StringParameter("problemName", args);
    referenceFrontFilename = new StringParameter("referenceFrontFileName", args);
    maximumNumberOfEvaluationsParameter =
        new PositiveIntegerValue("maximumNumberOfEvaluations", args);

    fixedParameterList.add(problemNameParameter);
    fixedParameterList.add(referenceFrontFilename);
    fixedParameterList.add(maximumNumberOfEvaluationsParameter);

    for (Parameter<?> parameter : fixedParameterList) {
      parameter.parse().check();
    }
    populationSizeParameter = new PositiveIntegerValue("populationSize", args);

    algorithmResult(args);
    createInitialSolution(args);
    selection(args);
    variation(args);

    autoConfigurableParameterList.add(populationSizeParameter);
    autoConfigurableParameterList.add(algorithmResultParameter);
    autoConfigurableParameterList.add(createInitialSolutionsParameter);
    autoConfigurableParameterList.add(variationParameter);
    autoConfigurableParameterList.add(selectionParameter);

    for (Parameter<?> parameter : autoConfigurableParameterList) {
      parameter.parse().check();
    }
  }

  private void variation(String[] args) {
    CrossoverParameter crossoverParameter = new CrossoverParameter(args,
        List.of("SBX", "BLX_ALPHA"));
    ProbabilityParameter crossoverProbability =
        new ProbabilityParameter("crossoverProbability", args);
    crossoverParameter.addGlobalParameter(crossoverProbability);
    RepairDoubleSolutionStrategyParameter crossoverRepairStrategy =
        new RepairDoubleSolutionStrategyParameter(
            "crossoverRepairStrategy", args, Arrays.asList("random", "round", "bounds"));
    crossoverParameter.addGlobalParameter(crossoverRepairStrategy);

    RealParameter distributionIndex = new RealParameter("sbxDistributionIndex", args, 5.0, 400.0);
    crossoverParameter.addSpecificParameter("SBX", distributionIndex);

    RealParameter alpha = new RealParameter("blxAlphaCrossoverAlphaValue", args, 0.0, 1.0);
    crossoverParameter.addSpecificParameter("BLX_ALPHA", alpha);

    MutationParameter mutationParameter =
        new MutationParameter(args,
            Arrays.asList("uniform", "polynomial", "linkedPolynomial", "nonUniform"));

    RealParameter mutationProbabilityFactor = new RealParameter("mutationProbabilityFactor", args,
        0.0, 2.0);
    mutationParameter.addGlobalParameter(mutationProbabilityFactor);
    RepairDoubleSolutionStrategyParameter mutationRepairStrategy =
        new RepairDoubleSolutionStrategyParameter(
            "mutationRepairStrategy", args, Arrays.asList("random", "round", "bounds"));
    mutationParameter.addGlobalParameter(mutationRepairStrategy);

    RealParameter distributionIndexForPolynomialMutation =
        new RealParameter("polynomialMutationDistributionIndex", args, 5.0, 400.0);
    mutationParameter.addSpecificParameter("polynomial", distributionIndexForPolynomialMutation);

    RealParameter distributionIndexForLinkedPolynomialMutation =
        new RealParameter("linkedPolynomialMutationDistributionIndex", args, 5.0, 400.0);
    mutationParameter.addSpecificParameter("linkedPolynomial",
        distributionIndexForLinkedPolynomialMutation);

    RealParameter uniformMutationPerturbation =
        new RealParameter("uniformMutationPerturbation", args, 0.0, 1.0);
    mutationParameter.addSpecificParameter("uniform", uniformMutationPerturbation);

    RealParameter nonUniformMutationPerturbation =
        new RealParameter("nonUniformMutationPerturbation", args, 0.0, 1.0);
    mutationParameter.addSpecificParameter("nonUniform", nonUniformMutationPerturbation);

    Problem<DoubleSolution> problem = ProblemFactory.loadProblem(problemNameParameter.getValue());
    mutationParameter.addNonConfigurableParameter("numberOfProblemVariables",
        problem.getNumberOfVariables());

    offspringPopulationSizeParameter = new IntegerParameter("offspringPopulationSize", args, 1,
        400);

    variationParameter =
        new VariationParameter(args, List.of("crossoverAndMutationVariation"));
    variationParameter.addGlobalParameter(offspringPopulationSizeParameter);
    variationParameter.addSpecificParameter("crossoverAndMutationVariation", crossoverParameter);
    variationParameter.addSpecificParameter("crossoverAndMutationVariation", mutationParameter);
  }

  private void selection(String[] args) {
    selectionParameter = new SelectionParameter<>(args, Arrays.asList("tournament", "random"));
    IntegerParameter selectionTournamentSize =
        new IntegerParameter("selectionTournamentSize", args, 2, 10);
    selectionParameter.addSpecificParameter("tournament", selectionTournamentSize);
  }

  private void createInitialSolution(String[] args) {
    createInitialSolutionsParameter =
        new CreateInitialSolutionsParameter(
            args, Arrays.asList("random", "latinHypercubeSampling", "scatterSearch"));
  }

  private void algorithmResult(String[] args) {
    algorithmResultParameter =
        new CategoricalParameter("algorithmResult", args, List.of("externalArchive", "population"));
    populationSizeWithArchiveParameter = new IntegerParameter("populationSizeWithArchive", args, 10,
        200);
    externalArchiveParameter = new ExternalArchiveParameter(args,
        List.of("crowdingDistanceArchive", "unboundedArchive"));
    algorithmResultParameter.addSpecificParameter(
        "externalArchive", populationSizeWithArchiveParameter);

    algorithmResultParameter.addSpecificParameter(
        "externalArchive", externalArchiveParameter);
  }

  /**
   * Creates an instance of NSGA-II from the parsed parameters
   *
   * @return
   */
  public EvolutionaryAlgorithm<DoubleSolution> create() {
    Problem<DoubleSolution> problem = ProblemFactory.loadProblem(problemNameParameter.getValue());

    Archive<DoubleSolution> archive = null;

    if (algorithmResultParameter.getValue().equals("externalArchive")) {
      externalArchiveParameter.setSize(populationSizeParameter.getValue());
      archive = externalArchiveParameter.getParameter();
      populationSizeParameter.setValue(populationSizeWithArchiveParameter.getValue());
    }

    Ranking<DoubleSolution> ranking = new FastNonDominatedSortRanking<>(
        new DominanceWithConstraintsComparator<>());
    DensityEstimator<DoubleSolution> densityEstimator = new CrowdingDistanceDensityEstimator<>();
    MultiComparator<DoubleSolution> rankingAndCrowdingComparator =
        new MultiComparator<>(
            Arrays.asList(
                Comparator.comparing(ranking::getRank),
                Comparator.comparing(densityEstimator::getValue).reversed()));

    SolutionsCreation<DoubleSolution> initialSolutionsCreation =
        createInitialSolutionsParameter.getParameter((DoubleProblem) problem,
            populationSizeParameter.getValue());

    MutationParameter mutationParameter = (MutationParameter) variationParameter.findSpecificParameter(
        "mutation");
    if (mutationParameter.getValue().equals("nonUniform")) {
      mutationParameter.addSpecificParameter("nonUniform", maximumNumberOfEvaluationsParameter);
      mutationParameter.addNonConfigurableParameter("maxIterations",
          maximumNumberOfEvaluationsParameter.getValue() / populationSizeParameter.getValue());
    }
    Variation<DoubleSolution> variation = variationParameter.getDoubleSolutionParameter();

    MatingPoolSelection<DoubleSolution> selection =
        selectionParameter.getParameter(
            variation.getMatingPoolSize(), rankingAndCrowdingComparator);

    Evaluation<DoubleSolution> evaluation ;
    if (algorithmResultParameter.getValue().equals("externalArchive")) {
      evaluation = new SequentialEvaluationWithArchive<>(problem, archive);
    } else {
      evaluation = new SequentialEvaluation<>(problem);
    }

    RankingAndDensityEstimatorPreference<DoubleSolution> preferenceForReplacement = new RankingAndDensityEstimatorPreference<>(
        ranking, densityEstimator);
    Replacement<DoubleSolution> replacement =
        new RankingAndDensityEstimatorReplacement<>(preferenceForReplacement,
            Replacement.RemovalPolicy.oneShot);

    Termination termination =
        new TerminationByEvaluations(maximumNumberOfEvaluationsParameter.getValue());

    class EvolutionaryAlgorithmWithArchive extends EvolutionaryAlgorithm<DoubleSolution> {
      private Archive<DoubleSolution> archive ;
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
          MatingPoolSelection<DoubleSolution> selection, Variation<DoubleSolution> variation,
          Replacement<DoubleSolution> replacement,
          Archive<DoubleSolution> archive) {
        super(name, initialPopulationCreation, evaluation, termination, selection, variation,
            replacement);
        this.archive = archive ;
      }

      @Override
      public List<DoubleSolution> getResult() {
        return archive.getSolutionList() ;
      }
    }

    if (algorithmResultParameter.getValue().equals("externalArchive")) {
      return new EvolutionaryAlgorithmWithArchive(
          "NSGA-II",
          initialSolutionsCreation,
          evaluation,
          termination,
          selection,
          variation,
          replacement,
          archive) ;
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
