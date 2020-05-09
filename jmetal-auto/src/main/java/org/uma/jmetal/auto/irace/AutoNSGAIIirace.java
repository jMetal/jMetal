package org.uma.jmetal.auto.irace;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.auto.parameter.IntegerParameter;
import org.uma.jmetal.auto.parameter.Parameter;
import org.uma.jmetal.auto.parameter.RealParameter;
import org.uma.jmetal.auto.parameter.catalogue.*;
import org.uma.jmetal.component.densityestimator.DensityEstimator;
import org.uma.jmetal.component.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.ranking.Ranking;
import org.uma.jmetal.component.ranking.impl.MergeNonDominatedSortRanking;
import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.component.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.selection.MatingPoolSelection;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.variation.Variation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.impl.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.PointSolution;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoNSGAIIirace {
  public List<Parameter<?>> autoConfigurableParameterList = new ArrayList<>();
  public List<Parameter<?>> fixedParameterList = new ArrayList<>();

  private ProblemNameParameter<DoubleSolution> problemNameParameter;
  private ReferenceFrontFilenameParameter referenceFrontFilename;
  private IntegerParameter maximumNumberOfEvaluationsParameter;
  private StringCategoricalParameter algorithmResultParameter;
  private PopulationSizeParameter populationSizeParameter;
  private PopulationSizeWithArchive populationSizeWithArchiveParameter;
  private OffspringPopulationSizeParameter offspringPopulationSizeParameter;
  private CreateInitialSolutionsParameter createInitialSolutionsParameter;
  private SelectionParameter selectionParameter;
  private VariationParameter variationParameter;

  public void parseAndCheckParameters(String[] args) {
    problemNameParameter = new ProblemNameParameter<>(args);
    populationSizeParameter = new PopulationSizeParameter(args);
    referenceFrontFilename = new ReferenceFrontFilenameParameter(args);
    maximumNumberOfEvaluationsParameter =
        new IntegerParameter("maximumNumberOfEvaluations", args, 1, 10000000);

    fixedParameterList.add(problemNameParameter);
    fixedParameterList.add(referenceFrontFilename);
    fixedParameterList.add(maximumNumberOfEvaluationsParameter);
    fixedParameterList.add(populationSizeParameter);

    for (Parameter<?> parameter : fixedParameterList) {
      parameter.parse().check();
    }

    algorithmResultParameter =
        new StringCategoricalParameter(
            "algorithmResult", args, Arrays.asList("externalArchive", "population"));
    populationSizeWithArchiveParameter =
        new PopulationSizeWithArchive(args, Arrays.asList(10, 20, 50, 100, 200));
    algorithmResultParameter.addSpecificParameter("population", populationSizeParameter);
    algorithmResultParameter.addSpecificParameter(
        "externalArchive", populationSizeWithArchiveParameter);

    createInitialSolutionsParameter =
        new CreateInitialSolutionsParameter(
            args, Arrays.asList("random", "latinHypercubeSampling", "scatterSearch"));

    selectionParameter = new SelectionParameter(args, Arrays.asList("tournament", "random"));
    IntegerParameter selectionTournamentSize =
        new IntegerParameter("selectionTournamentSize", args, 2, 10);
    selectionParameter.addSpecificParameter("tournament", selectionTournamentSize);

    CrossoverParameter crossover = new CrossoverParameter(args, Arrays.asList("SBX", "BLX_ALPHA"));
    ProbabilityParameter crossoverProbability =
        new ProbabilityParameter("crossoverProbability", args);
    crossover.addGlobalParameter(crossoverProbability);
    RepairDoubleSolutionStrategyParameter crossoverRepairStrategy =
        new RepairDoubleSolutionStrategyParameter(
            "crossoverRepairStrategy", args, Arrays.asList("random", "round", "bounds"));
    crossover.addGlobalParameter(crossoverRepairStrategy);

    RealParameter distributionIndex = new RealParameter("sbxDistributionIndex", args, 5.0, 400.0);
    crossover.addSpecificParameter("SBX", distributionIndex);

    RealParameter alpha = new RealParameter("blxAlphaCrossoverAlphaValue", args, 0.0, 1.0);
    crossover.addSpecificParameter("BLX_ALPHA", alpha);

    MutationParameter mutation =
        new MutationParameter(args, Arrays.asList("uniform", "polynomial"));
    ProbabilityParameter mutationProbability =
        new ProbabilityParameter("mutationProbability", args);
    mutation.addGlobalParameter(mutationProbability);
    RepairDoubleSolutionStrategyParameter mutationRepairStrategy =
        new RepairDoubleSolutionStrategyParameter(
            "mutationRepairStrategy", args, Arrays.asList("random", "round", "bounds"));
    mutation.addGlobalParameter(mutationRepairStrategy);

    RealParameter distributionIndexForMutation =
        new RealParameter("polynomialMutationDistributionIndex", args, 5.0, 400.0);
    mutation.addSpecificParameter("polynomial", distributionIndexForMutation);

    RealParameter uniformMutationPerturbation =
        new RealParameter("uniformMutationPerturbation", args, 0.0, 1.0);
    mutation.addSpecificParameter("uniform", uniformMutationPerturbation);

    DifferentialEvolutionCrossoverParameter differentialEvolutionCrossover =
        new DifferentialEvolutionCrossoverParameter(args);

    RealParameter f = new RealParameter("f", args, 0.0, 1.0);
    RealParameter cr = new RealParameter("cr", args, 0.0, 1.0);
    differentialEvolutionCrossover.addGlobalParameter(f);
    differentialEvolutionCrossover.addGlobalParameter(cr);

    offspringPopulationSizeParameter =
        new OffspringPopulationSizeParameter(args, Arrays.asList(1, 10, 50, 100, 200, 400));

    variationParameter = new VariationParameter(args, List.of("crossoverAndMutationVariation"));
    variationParameter.addGlobalParameter(offspringPopulationSizeParameter);
    variationParameter.addSpecificParameter("crossoverAndMutationVariation", crossover);
    variationParameter.addSpecificParameter("crossoverAndMutationVariation", mutation);

    autoConfigurableParameterList.add(algorithmResultParameter);
    autoConfigurableParameterList.add(offspringPopulationSizeParameter);
    autoConfigurableParameterList.add(createInitialSolutionsParameter);
    autoConfigurableParameterList.add(variationParameter);
    autoConfigurableParameterList.add(selectionParameter);

    for (Parameter<?> parameter : autoConfigurableParameterList) {
      parameter.parse().check();
    }
  }

  /**
   * Creates an instance of NSGA-II from the parsed parameters
   *
   * @return
   */
  EvolutionaryAlgorithm<DoubleSolution> create() {
    DoubleProblem problem = (DoubleProblem) problemNameParameter.getProblem();

    Archive<DoubleSolution> archive = null;
    if (algorithmResultParameter.getValue().equals("externalArchive")) {
      archive = new CrowdingDistanceArchive<>(populationSizeParameter.getValue());
      populationSizeParameter.setValue(populationSizeWithArchiveParameter.getValue());
    }

    Ranking<DoubleSolution> ranking =
        new MergeNonDominatedSortRanking<>();
    DensityEstimator<DoubleSolution> densityEstimator = new CrowdingDistanceDensityEstimator<>();
    var rankingAndCrowdingComparator =
        new MultiComparator<>(
            List.of(ranking.getSolutionComparator(), densityEstimator.getSolutionComparator()));

    var initialSolutionsCreation =
        createInitialSolutionsParameter.getParameter(problem, populationSizeParameter.getValue());
    var variation = (Variation<DoubleSolution>) variationParameter.getParameter();
    var selection =
        (MatingPoolSelection<DoubleSolution>)
            selectionParameter.getParameter(
                variation.getMatingPoolSize(), rankingAndCrowdingComparator);

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem);

    Replacement<DoubleSolution> replacement =
        new RankingAndDensityEstimatorReplacement<>(ranking, densityEstimator);

    Termination termination =
        new TerminationByEvaluations(maximumNumberOfEvaluationsParameter.getValue());

    var nsgaii =
        new EvolutionaryAlgorithm<>(
            "NSGAII",
            evaluation,
            initialSolutionsCreation,
            termination,
            selection,
            variation,
            replacement,
            archive);
    return nsgaii;
  }

  public static void main(String[] args) throws FileNotFoundException {
    AutoNSGAIIirace nsgaiiWithParameters = new AutoNSGAIIirace();
    nsgaiiWithParameters.parseAndCheckParameters(args);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = nsgaiiWithParameters.create();
    nsgaII.run();

    String referenceFrontFile =
        "resources/referenceFrontsCSV/" + nsgaiiWithParameters.referenceFrontFilename.getValue();
    Front referenceFront = new ArrayFront(referenceFrontFile);

    FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront);
    Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront);
    Front normalizedFront = frontNormalizer.normalize(new ArrayFront(nsgaII.getResult()));
    List<PointSolution> normalizedPopulation =
        FrontUtils.convertFrontToSolutionList(normalizedFront);

    double referenceFrontHV =
        new PISAHypervolume<PointSolution>(normalizedReferenceFront)
            .evaluate(FrontUtils.convertFrontToSolutionList(normalizedReferenceFront));
    double obtainedFrontHV =
        new PISAHypervolume<PointSolution>(normalizedReferenceFront).evaluate(normalizedPopulation);
    System.out.println((referenceFrontHV - obtainedFrontHV) / referenceFrontHV);
  }
}
