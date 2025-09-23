package org.uma.jmetal.auto.autoconfigurablealgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.uma.jmetal.auto.parameter.*;
import org.uma.jmetal.auto.parameter.catalogue.CreateInitialSolutionsParameter;
import org.uma.jmetal.auto.parameter.catalogue.CrossoverParameter;
import org.uma.jmetal.auto.parameter.catalogue.ExternalArchiveParameter;
import org.uma.jmetal.auto.parameter.catalogue.MutationParameter;
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
import org.uma.jmetal.component.catalogue.ea.replacement.impl.SMSEMOAReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.problem.permutationproblem.PermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/**
 * Class to configure SMS-EMOA with an argument string using class {@link EvolutionaryAlgorithm}
 *
 * @autor Antonio J. Nebro
 */
public class AutoSMSEMOAPermutation implements AutoConfigurableAlgorithm {
  private List<Parameter<?>> configurableParameterList = new ArrayList<>();
  private List<Parameter<?>> fixedParameterList = new ArrayList<>();
  private StringParameter problemNameParameter;
  public StringParameter referenceFrontFilename;
  private PositiveIntegerValue randomGeneratorSeedParameter;
  private PositiveIntegerValue maximumNumberOfEvaluationsParameter;
  private PositiveIntegerValue offspringPopulationSizeParameter;
  private CategoricalParameter algorithmResultParameter;
  private ExternalArchiveParameter<PermutationSolution<Integer>> externalArchiveParameter;
  private PositiveIntegerValue populationSizeParameter;
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

  public AutoSMSEMOAPermutation() {
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
    CrossoverParameter crossoverParameter =
            new CrossoverParameter(List.of("PMX", "CX", "OXD", "positionBased", "edgeRecombination"));

    RealParameter crossoverProbability = new RealParameter("crossoverProbability", 0.6, 0.9);
    crossoverParameter.addGlobalParameter(crossoverProbability);

    MutationParameter mutationParameter =
            new MutationParameter(List.of("swap", "displacement", "insert", "scramble", "inversion", "simpleInversion"));

    RealParameter mutationProbability =
            new RealParameter("mutationProbability", 0.05, 0.1);
    mutationParameter.addGlobalParameter(mutationProbability);

    offspringPopulationSizeParameter = new PositiveIntegerValue("offspringPopulationSize") ;
    offspringPopulationSizeParameter.value(1) ;

    variationParameter = new VariationParameter(List.of("crossoverAndMutationVariation"));
    variationParameter.addSpecificParameter(
            "crossoverAndMutationVariation", offspringPopulationSizeParameter);
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
    createInitialSolutionsParameter = new CreateInitialSolutionsParameter(List.of("random"));
  }

  private void algorithmResult() {
    algorithmResultParameter =
        new CategoricalParameter("algorithmResult", List.of("externalArchive", "population"));
    externalArchiveParameter = new ExternalArchiveParameter(
        List.of("crowdingDistanceArchive", "unboundedArchive"));

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
   * Creates an instance of SMS-EMOA from the parsed parameters
   *
   * @return
   */
  public EvolutionaryAlgorithm<PermutationSolution<Integer>> create() {
    JMetalRandom.getInstance().setSeed(randomGeneratorSeedParameter.value());

    var problem = (PermutationProblem<PermutationSolution<Integer>>) problem();

    Archive<PermutationSolution<Integer>> archive = null;

    if (algorithmResultParameter.value().equals("externalArchive")) {
      externalArchiveParameter.setSize(populationSizeParameter.value());
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
            (SolutionsCreation<PermutationSolution<Integer>>)
                    createInitialSolutionsParameter.getParameter(problem, populationSizeParameter.value());
                    MutationParameter mutationParameter =
                    (MutationParameter) variationParameter.findSpecificParameter("mutation");
                mutationParameter.addNonConfigurableParameter("permutationLength", problem.numberOfVariables());
                
    var variation = (Variation<PermutationSolution<Integer>>) variationParameter.getPermutationSolutionParameter();

    Selection<PermutationSolution<Integer>> selection =
        selectionParameter.getParameter(
            variation.matingPoolSize(), rankingAndCrowdingComparator);

    Evaluation<PermutationSolution<Integer>> evaluation;
    if (algorithmResultParameter.value().equals("externalArchive")) {
      evaluation = new SequentialEvaluationWithArchive<>(problem, archive);
    } else {
      evaluation = new SequentialEvaluation<>(problem);
    }

    Hypervolume<PermutationSolution<Integer>> hypervolume = new PISAHypervolume<>();
    var replacement = new SMSEMOAReplacement<>(ranking, hypervolume);
    Termination termination =
        new TerminationByEvaluations(maximumNumberOfEvaluationsParameter.value());

    class EvolutionaryAlgorithmWithArchive extends EvolutionaryAlgorithm<PermutationSolution<Integer>> {

      private Archive<PermutationSolution<Integer>> archive;

      /**
       * Constructor
       *
       * @param name Algorithm name
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
          "SMS-EMOA",
          initialSolutionsCreation,
          evaluation,
          termination,
          selection,
          variation,
          replacement,
          archive);
    } else {
      return new EvolutionaryAlgorithm<>(
        "SMS-EMOA",
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
