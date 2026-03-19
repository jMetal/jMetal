package org.uma.jmetal.auto.autoconfigurablealgorithm;

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
import org.uma.jmetal.auto.parameter.catalogue.MutationParameter;
import org.uma.jmetal.auto.parameter.catalogue.ProbabilityParameter;
import org.uma.jmetal.auto.parameter.catalogue.RepairDoubleSolutionStrategyParameter;
import org.uma.jmetal.auto.parameter.catalogue.SelectionParameter;
import org.uma.jmetal.auto.parameter.catalogue.VariationParameter;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.AGEMOEABuilder;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.AGEMOEAReplacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea.AGEMOEA2EnvironmentalSelection;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea.AGEMOEAEnvironmentalSelection;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea.SurvivalScoreComparator;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class to configure AGE-MOEA with an argument string using class {@link EvolutionaryAlgorithm}.
 *
 * <p>The AGE family variant and the environmental selection are independently configurable so they
 * can be tuned with auto-configuration tools such as irace.
 */
public class AutoAGEMOEA implements AutoConfigurableAlgorithm {
  private final List<Parameter<?>> configurableParameterList = new ArrayList<>();
  private final List<Parameter<?>> fixedParameterList = new ArrayList<>();
  private StringParameter problemNameParameter;
  public StringParameter referenceFrontFilename;
  private PositiveIntegerValue randomGeneratorSeedParameter;
  private PositiveIntegerValue maximumNumberOfEvaluationsParameter;
  private PositiveIntegerValue populationSizeParameter;
  private CategoricalParameter algorithmVariantParameter;
  private CategoricalParameter environmentalSelectionParameter;
  private CategoricalParameter replacementParameter;
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

  public AutoAGEMOEA() {
    configure();
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

    algorithmVariant();
    environmentalSelection();
    replacement();
    createInitialSolution();
    selection();
    variation();

    configurableParameterList.add(algorithmVariantParameter);
    configurableParameterList.add(environmentalSelectionParameter);
    configurableParameterList.add(replacementParameter);
    configurableParameterList.add(createInitialSolutionsParameter);
    configurableParameterList.add(variationParameter);
    configurableParameterList.add(selectionParameter);
  }

  private void algorithmVariant() {
    algorithmVariantParameter =
        new CategoricalParameter("algorithmVariant", List.of("agemoea", "agemoeaii"));
  }

  private void environmentalSelection() {
    environmentalSelectionParameter =
        new CategoricalParameter("environmentalSelection", List.of("agemoea", "agemoeaii"));
  }

  private void replacement() {
    replacementParameter =
        new CategoricalParameter("replacement", List.of("agemoeaReplacement"));
  }

  private void variation() {
    CrossoverParameter crossoverParameter =
        new CrossoverParameter(List.of("SBX", "BLX_ALPHA", "wholeArithmetic"));
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

    CategoricalIntegerParameter offspringPopulationSizeParameter =
        new CategoricalIntegerParameter(
            "offspringPopulationSize",
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
        new CreateInitialSolutionsParameter(
            Arrays.asList("random", "latinHypercubeSampling", "scatterSearch"));
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
   * Creates an instance of AGE-MOEA from the parsed parameters.
   *
   * @return the configured evolutionary algorithm
   */
  public EvolutionaryAlgorithm<DoubleSolution> create() {
    JMetalRandom.getInstance().setSeed(randomGeneratorSeedParameter.value());

    DoubleProblem problem = (DoubleProblem) problem();
    var initialSolutionsCreation =
        (SolutionsCreation<DoubleSolution>)
            createInitialSolutionsParameter.getParameter(problem, populationSizeParameter.value());

    MutationParameter mutationParameter =
        (MutationParameter) variationParameter.findSpecificParameter("mutation");
    mutationParameter.addNonConfigurableParameter(
        "numberOfProblemVariables", problem.numberOfVariables());

    if (mutationParameter.value().equals("nonUniform")) {
      mutationParameter.addSpecificParameter("nonUniform", maximumNumberOfEvaluationsParameter);
      mutationParameter.addNonConfigurableParameter(
          "maxIterations",
          maximumNumberOfEvaluationsParameter.value() / populationSizeParameter.value());
    }

    CrossoverParameter crossoverParameter =
        (CrossoverParameter) variationParameter.findSpecificParameter("crossover");
    CrossoverOperator<DoubleSolution> crossover = crossoverParameter.getDoubleSolutionParameter();
    MutationOperator<DoubleSolution> mutation = mutationParameter.getDoubleSolutionParameter();

    Variation<DoubleSolution> variation =
        (Variation<DoubleSolution>) variationParameter.getDoubleSolutionParameter();

    AGEMOEAEnvironmentalSelection<DoubleSolution> environmentalSelection =
        environmentalSelection(problem.numberOfObjectives());

    AGEMOEABuilder<DoubleSolution> builder =
        new AGEMOEABuilder<>(
            problem,
            populationSizeParameter.value(),
            variation.offspringPopulationSize(),
            crossover,
            mutation,
            variant())
            .setCreateInitialPopulation(initialSolutionsCreation)
            .setTermination(new TerminationByEvaluations(maximumNumberOfEvaluationsParameter.value()))
            .setEnvironmentalSelection(environmentalSelection)
            .setReplacement(replacement(environmentalSelection));

    if ("tournament".equals(selectionParameter.value())) {
      int tournamentSize =
          (Integer) selectionParameter.findSpecificParameter("selectionTournamentSize").value();
      builder.setTournamentSize(tournamentSize);
    } else {
      Selection<DoubleSolution> selection =
          selectionParameter.getParameter(variation.matingPoolSize(), new SurvivalScoreComparator<>());
      builder.setSelection(selection);
    }

    return builder.build();
  }

  private AGEMOEABuilder.Variant variant() {
    return switch (algorithmVariantParameter.value()) {
      case "agemoea" -> AGEMOEABuilder.Variant.AGEMOEA;
      case "agemoeaii" -> AGEMOEABuilder.Variant.AGEMOEAII;
      default -> throw new JMetalException(
          "Algorithm variant unknown: " + algorithmVariantParameter.value());
    };
  }

  private AGEMOEAEnvironmentalSelection<DoubleSolution> environmentalSelection(
      int numberOfObjectives) {
    return switch (environmentalSelectionParameter.value()) {
      case "agemoea" -> new AGEMOEAEnvironmentalSelection<>(numberOfObjectives);
      case "agemoeaii" -> new AGEMOEA2EnvironmentalSelection<>(numberOfObjectives);
      default -> throw new JMetalException(
          "Environmental selection unknown: " + environmentalSelectionParameter.value());
    };
  }

  private Replacement<DoubleSolution> replacement(
      AGEMOEAEnvironmentalSelection<DoubleSolution> environmentalSelection) {
    return switch (replacementParameter.value()) {
      case "agemoeaReplacement" -> new AGEMOEAReplacement<>(environmentalSelection);
      default -> throw new JMetalException(
          "Replacement component unknown: " + replacementParameter.value());
    };
  }

  public static void print(List<Parameter<?>> parameterList) {
    parameterList.forEach(System.out::println);
  }
}
