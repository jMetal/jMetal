package org.uma.jmetal.auto.autoconfigurablealgorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.uma.jmetal.auto.parameter.catalogue.RVEAReplacementParameter;
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
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.RandomSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.referencepoint.ReferencePointGenerator;

/**
 * Class to configure RVEA, RVEA* and iRVEA with an argument string using class {@link
 * EvolutionaryAlgorithm}. The variant is the replacement component ({@code --replacement rvea |
 * rveaStar | iRVEA}).
 *
 * <p>The population size is not a parameter: it is the number of reference vectors. By default
 * the vectors are generated from the number of objectives M (simplex lattice with H = 99, 13, 7
 * and 5 divisions for M = 2, 3, 4 and 5, giving 100, 105, 120 and 126 vectors, and two layers with
 * H = 3 and 2 for M >= 6). The optional parameter {@code --referenceVectorsFile} gives a file with
 * one weight vector per line (e.g., {@code resources/weightVectorFiles/moead/W3D_100.dat}), so any
 * population size can be used.
 *
 * <p>RVEA supports only unconstrained problems.
 *
 * @author Antonio J. Nebro
 */
public class AutoRVEA implements AutoConfigurableAlgorithm {
  private static final String REFERENCE_VECTORS_FILE = "referenceVectorsFile";

  private final List<Parameter<?>> configurableParameterList = new ArrayList<>();
  private final List<Parameter<?>> fixedParameterList = new ArrayList<>();
  private StringParameter problemNameParameter;
  public StringParameter referenceFrontFilename;
  private PositiveIntegerValue randomGeneratorSeedParameter;
  private PositiveIntegerValue maximumNumberOfEvaluationsParameter;
  private StringParameter referenceVectorsFileParameter;
  private CategoricalParameter algorithmResultParameter;
  private ExternalArchiveParameter<DoubleSolution> externalArchiveParameter;
  private CreateInitialSolutionsParameter createInitialSolutionsParameter;
  private SelectionParameter<DoubleSolution> selectionParameter;
  private IntegerParameter selectionTournamentSizeParameter;
  private VariationParameter variationParameter;
  private CategoricalIntegerParameter offspringPopulationSizeParameter;
  private RVEAReplacementParameter replacementParameter;

  @Override
  public List<Parameter<?>> configurableParameterList() {
    return configurableParameterList;
  }

  @Override
  public List<Parameter<?>> fixedParameterList() {
    return fixedParameterList;
  }

  public AutoRVEA() {
    this.configure();
  }

  private void configure() {
    problemNameParameter = new StringParameter("problemName");
    randomGeneratorSeedParameter = new PositiveIntegerValue("randomGeneratorSeed");
    referenceFrontFilename = new StringParameter("referenceFrontFileName");
    maximumNumberOfEvaluationsParameter = new PositiveIntegerValue("maximumNumberOfEvaluations");
    referenceVectorsFileParameter = new StringParameter(REFERENCE_VECTORS_FILE);

    fixedParameterList.add(problemNameParameter);
    fixedParameterList.add(referenceFrontFilename);
    fixedParameterList.add(maximumNumberOfEvaluationsParameter);
    fixedParameterList.add(randomGeneratorSeedParameter);

    algorithmResult();
    createInitialSolution();
    variation();
    selection();
    replacement();

    configurableParameterList.add(algorithmResultParameter);
    configurableParameterList.add(createInitialSolutionsParameter);
    configurableParameterList.add(variationParameter);
    configurableParameterList.add(selectionParameter);
    configurableParameterList.add(replacementParameter);
  }

  private void replacement() {
    replacementParameter = new RVEAReplacementParameter(List.of("rvea", "rveaStar", "iRVEA"));
    replacementParameter.addGlobalParameter(new RealParameter("alpha", 0.5, 10.0));
    replacementParameter.addGlobalParameter(new RealParameter("fr", 0.01, 1.0));
    replacementParameter.addSpecificParameter(
        "iRVEA", new IntegerParameter("numberOfSubregions", 10, 100));
    replacementParameter.addSpecificParameter(
        "iRVEA", new RealParameter("lateStageFraction", 0.5, 1.0));
    replacementParameter.addSpecificParameter(
        "iRVEA", new RealParameter("epsilonKappa", 0.01, 0.2));
  }

  private void variation() {
    CrossoverParameter crossoverParameter =
        new CrossoverParameter(List.of("SBX", "BLX_ALPHA", "wholeArithmetic"));
    ProbabilityParameter crossoverProbability = new ProbabilityParameter("crossoverProbability");
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

    RealParameter mutationProbabilityFactor =
        new RealParameter("mutationProbabilityFactor", 0.0, 2.0);
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
    mutationParameter.addSpecificParameter(
        "linkedPolynomial", distributionIndexForLinkedPolynomialMutation);

    RealParameter uniformMutationPerturbation =
        new RealParameter("uniformMutationPerturbation", 0.0, 1.0);
    mutationParameter.addSpecificParameter("uniform", uniformMutationPerturbation);

    RealParameter nonUniformMutationPerturbation =
        new RealParameter("nonUniformMutationPerturbation", 0.0, 1.0);
    mutationParameter.addSpecificParameter("nonUniform", nonUniformMutationPerturbation);

    offspringPopulationSizeParameter =
        new CategoricalIntegerParameter(
            "offspringPopulationSize", List.of(1, 2, 5, 10, 20, 50, 100, 150, 200, 300, 400));

    variationParameter = new VariationParameter(List.of("crossoverAndMutationVariation"));
    variationParameter.addSpecificParameter(
        "crossoverAndMutationVariation", offspringPopulationSizeParameter);
    variationParameter.addSpecificParameter("crossoverAndMutationVariation", crossoverParameter);
    variationParameter.addSpecificParameter("crossoverAndMutationVariation", mutationParameter);
  }

  private void selection() {
    selectionParameter = new SelectionParameter<>(Arrays.asList("random", "tournament"));
    selectionTournamentSizeParameter = new IntegerParameter("selectionTournamentSize", 2, 10);
    selectionParameter.addSpecificParameter("tournament", selectionTournamentSizeParameter);
  }

  private void createInitialSolution() {
    createInitialSolutionsParameter =
        new CreateInitialSolutionsParameter(
            Arrays.asList("random", "latinHypercubeSampling", "scatterSearch"));
  }

  private void algorithmResult() {
    algorithmResultParameter =
        new CategoricalParameter("algorithmResult", List.of("population", "externalArchive"));
    externalArchiveParameter =
        new ExternalArchiveParameter<>(List.of("crowdingDistanceArchive", "unboundedArchive"));
    algorithmResultParameter.addSpecificParameter("externalArchive", externalArchiveParameter);
  }

  @Override
  public void parse(String[] arguments) {
    for (Parameter<?> parameter : fixedParameterList) {
      parameter.parse(arguments).check();
    }
    referenceVectorsFileParameter.value(null);
    if (Arrays.asList(arguments).contains("--" + REFERENCE_VECTORS_FILE)) {
      referenceVectorsFileParameter.parse(arguments).check();
    }
    for (Parameter<?> parameter : configurableParameterList()) {
      parameter.parse(arguments).check();
    }
  }

  protected Problem<DoubleSolution> problem() {
    return ProblemFactory.loadProblem(problemNameParameter.value());
  }

  /**
   * Returns the reference vectors: those of the file given with {@code --referenceVectorsFile}, or
   * the default ones for the number of objectives otherwise.
   */
  List<double[]> referenceVectors(int numberOfObjectives) {
    List<double[]> vectors;
    String fileName = referenceVectorsFileParameter.value();
    if (fileName == null) {
      vectors = defaultReferenceVectors(numberOfObjectives);
    } else {
      try {
        vectors = Arrays.asList(VectorUtils.readVectors(fileName));
      } catch (IOException exception) {
        throw new JMetalException("Error reading the reference vectors file " + fileName, exception);
      }
      Check.that(!vectors.isEmpty(), "The reference vectors file " + fileName + " is empty");
      Check.that(
          vectors.stream().allMatch(vector -> vector.length == numberOfObjectives),
          "The vectors of file "
              + fileName
              + " must have "
              + numberOfObjectives
              + " components (the number of objectives)");
    }
    return vectors;
  }

  static List<double[]> defaultReferenceVectors(int numberOfObjectives) {
    return switch (numberOfObjectives) {
      case 2 -> ReferencePointGenerator.generateSingleLayer(2, 99);
      case 3 -> ReferencePointGenerator.generateSingleLayer(3, 13);
      case 4 -> ReferencePointGenerator.generateSingleLayer(4, 7);
      case 5 -> ReferencePointGenerator.generateSingleLayer(5, 5);
      default -> ReferencePointGenerator.generateTwoLayers(numberOfObjectives, 3, 2);
    };
  }

  /**
   * Creates an instance of RVEA, RVEA* or iRVEA from the parsed parameters
   *
   * @return The algorithm
   */
  public EvolutionaryAlgorithm<DoubleSolution> create() {
    JMetalRandom.getInstance().setSeed(randomGeneratorSeedParameter.value());

    Problem<DoubleSolution> problem = problem();
    Check.that(problem.numberOfConstraints() == 0, "RVEA requires an unconstrained problem");

    List<double[]> referenceVectors = referenceVectors(problem.numberOfObjectives());
    int populationSize = referenceVectors.size();
    int maximumNumberOfEvaluations = maximumNumberOfEvaluationsParameter.value();
    Check.that(
        maximumNumberOfEvaluations >= populationSize,
        "The budget must cover the initial population");

    Archive<DoubleSolution> archive = null;
    if (algorithmResultParameter.value().equals("externalArchive")) {
      externalArchiveParameter.setSize(populationSize);
      archive = externalArchiveParameter.getParameter();
    }

    var initialSolutionsCreation =
        (SolutionsCreation<DoubleSolution>)
            createInitialSolutionsParameter.getParameter((DoubleProblem) problem, populationSize);

    MutationParameter mutationParameter =
        (MutationParameter) variationParameter.findSpecificParameter("mutation");
    mutationParameter.addNonConfigurableParameter(
        "numberOfProblemVariables", problem.numberOfVariables());

    if (mutationParameter.value().equals("nonUniform")) {
      mutationParameter.addSpecificParameter("nonUniform", maximumNumberOfEvaluationsParameter);
      mutationParameter.addNonConfigurableParameter(
          "maxIterations", maximumNumberOfEvaluations / populationSize);
    }

    var variation = (Variation<DoubleSolution>) variationParameter.getDoubleSolutionParameter();

    Selection<DoubleSolution> selection = selection(variation.matingPoolSize());

    Evaluation<DoubleSolution> evaluation;
    if (algorithmResultParameter.value().equals("externalArchive")) {
      evaluation = new SequentialEvaluationWithArchive<>(problem, archive);
    } else {
      evaluation = new SequentialEvaluation<>(problem);
    }

    int offspringPopulationSize = variation.offspringPopulationSize();
    int maxGenerations =
        Math.max(
            1,
            (int)
                Math.ceil(
                    (double) (maximumNumberOfEvaluations - populationSize)
                        / offspringPopulationSize));
    replacementParameter.addNonConfigurableParameter(
        "numberOfObjectives", problem.numberOfObjectives());
    replacementParameter.addNonConfigurableParameter("maxGenerations", maxGenerations);
    replacementParameter.addNonConfigurableParameter("referenceVectors", referenceVectors);
    Replacement<DoubleSolution> replacement = replacementParameter.getParameter();

    Termination termination = new TerminationByEvaluations(maximumNumberOfEvaluations);
    String name = replacementParameter.algorithmName();

    class EvolutionaryAlgorithmWithArchive extends EvolutionaryAlgorithm<DoubleSolution> {
      private final Archive<DoubleSolution> archive;

      public EvolutionaryAlgorithmWithArchive(
          String name,
          SolutionsCreation<DoubleSolution> initialPopulationCreation,
          Evaluation<DoubleSolution> evaluation,
          Termination termination,
          Selection<DoubleSolution> selection,
          Variation<DoubleSolution> variation,
          Replacement<DoubleSolution> replacement,
          Archive<DoubleSolution> archive) {
        super(
            name,
            initialPopulationCreation,
            evaluation,
            termination,
            selection,
            variation,
            replacement);
        this.archive = archive;
      }

      @Override
      public List<DoubleSolution> result() {
        return archive.solutions();
      }
    }

    if (algorithmResultParameter.value().equals("externalArchive")) {
      return new EvolutionaryAlgorithmWithArchive(
          name,
          initialSolutionsCreation,
          evaluation,
          termination,
          selection,
          variation,
          replacement,
          archive);
    } else {
      return new EvolutionaryAlgorithm<>(
          name,
          initialSolutionsCreation,
          evaluation,
          termination,
          selection,
          variation,
          replacement);
    }
  }

  /**
   * RVEA populations can shrink below the tournament size (empty niches contribute no survivor),
   * so tournament selection falls back to random selection in that case.
   */
  private Selection<DoubleSolution> selection(int matingPoolSize) {
    Selection<DoubleSolution> result;
    if (selectionParameter.value().equals("tournament")) {
      int tournamentSize = selectionTournamentSizeParameter.value();
      Selection<DoubleSolution> tournament =
          selectionParameter.getParameter(matingPoolSize, new DefaultDominanceComparator<>());
      Selection<DoubleSolution> random = new RandomSelection<>(matingPoolSize);
      result =
          population ->
              population.size() < tournamentSize
                  ? random.select(population)
                  : tournament.select(population);
    } else {
      result = selectionParameter.getParameter(matingPoolSize, null);
    }
    return result;
  }

  public static void print(List<Parameter<?>> parameterList) {
    parameterList.forEach(System.out::println);
  }
}
