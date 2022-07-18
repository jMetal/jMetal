package org.uma.jmetal.auto.autoconfigurablealgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.auto.parameter.IntegerParameter;
import org.uma.jmetal.auto.parameter.Parameter;
import org.uma.jmetal.auto.parameter.PositiveIntegerValue;
import org.uma.jmetal.auto.parameter.RealParameter;
import org.uma.jmetal.auto.parameter.StringParameter;
import org.uma.jmetal.auto.parameter.catalogue.CreateInitialSolutionsParameter;
import org.uma.jmetal.auto.parameter.catalogue.ExternalArchiveParameter;
import org.uma.jmetal.auto.parameter.catalogue.GlobalBestInitializationParameter;
import org.uma.jmetal.auto.parameter.catalogue.GlobalBestSelectionParameter;
import org.uma.jmetal.auto.parameter.catalogue.GlobalBestUpdateParameter;
import org.uma.jmetal.auto.parameter.catalogue.InertiaWeightComputingParameter;
import org.uma.jmetal.auto.parameter.catalogue.LocalBestInitializationParameter;
import org.uma.jmetal.auto.parameter.catalogue.LocalBestUpdateParameter;
import org.uma.jmetal.auto.parameter.catalogue.MutationParameter;
import org.uma.jmetal.auto.parameter.catalogue.PerturbationParameter;
import org.uma.jmetal.auto.parameter.catalogue.PositionUpdateParameter;
import org.uma.jmetal.auto.parameter.catalogue.RepairDoubleSolutionStrategyParameter;
import org.uma.jmetal.auto.parameter.catalogue.VelocityUpdateParameter;
import org.uma.jmetal.component.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.pso.globalbestinitialization.GlobalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.globalbestselection.GlobalBestSelection;
import org.uma.jmetal.component.catalogue.pso.globalbestupdate.GlobalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.InertiaWeightComputingStrategy;
import org.uma.jmetal.component.catalogue.pso.localbestinitialization.LocalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.localbestupdate.LocalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.positionupdate.PositionUpdate;
import org.uma.jmetal.component.catalogue.pso.velocityinitialization.impl.DefaultVelocityInitialization;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;

/**
 * Class to configure a generic MOPSO with an argument string using class {@link
 * ParticleSwarmOptimizationAlgorithm}
 *
 * @author Daniel Doblas
 */
public class AutoMOPSO implements AutoConfigurableAlgorithm{
  public List<Parameter<?>> autoConfigurableParameterList = new ArrayList<>();
  public List<Parameter<?>> fixedParameterList = new ArrayList<>();

  private StringParameter problemNameParameter;
  public StringParameter referenceFrontFilenameParameter;
  public ExternalArchiveParameter<DoubleSolution> leaderArchiveParameter;
  public CategoricalParameter velocityInitializationParameter;
  private PositiveIntegerValue maximumNumberOfEvaluationsParameter;
  private PositiveIntegerValue archiveSizeParameter;
  private IntegerParameter swarmSizeParameter;
  private LocalBestInitializationParameter localBestInitializationParameter;
  private GlobalBestInitializationParameter globalBestInitializationParameter;
  private GlobalBestSelectionParameter globalBestSelectionParameter;
  private PerturbationParameter perturbationParameter;
  private PositionUpdateParameter positionUpdateParameter;
  private GlobalBestUpdateParameter globalBestUpdateParameter;
  private LocalBestUpdateParameter localBestUpdateParameter;
  private VelocityUpdateParameter velocityUpdateParameter;
  private MutationParameter mutationParameter;

  private InertiaWeightComputingParameter inertiaWeightComputingParameter;

  @Override
  public List<Parameter<?>> getAutoConfigurableParameterList() {
    return autoConfigurableParameterList ;
  }

  @Override
  public void parseAndCheckParameters(String[] args) {
    problemNameParameter = new StringParameter("problemName", args);

    referenceFrontFilenameParameter = new StringParameter("referenceFrontFileName", args);
    maximumNumberOfEvaluationsParameter =
        new PositiveIntegerValue("maximumNumberOfEvaluations", args);

    fixedParameterList.add(problemNameParameter);
    fixedParameterList.add(referenceFrontFilenameParameter);
    fixedParameterList.add(maximumNumberOfEvaluationsParameter);

    for (Parameter<?> parameter : fixedParameterList) {
      parameter.parse().check();
    }

    swarmSizeParameter = new IntegerParameter("swarmSize", args, 10, 200);
    archiveSizeParameter = new PositiveIntegerValue("archiveSize", args);

    CreateInitialSolutionsParameter swarmInitializationParameter = new CreateInitialSolutionsParameter("swarmInitialization",
            args, Arrays.asList("random", "latinHypercubeSampling", "scatterSearch"));

    velocityInitializationParameter =
        new CategoricalParameter(
            "velocityInitialization", args, List.of("defaultVelocityInitialization"));

    velocityUpdateParameter = configureVelocityUpdate(args);

    localBestInitializationParameter = new LocalBestInitializationParameter(args,
        List.of("defaultLocalBestInitialization"));
    localBestUpdateParameter = new LocalBestUpdateParameter(args,
        Arrays.asList("defaultLocalBestUpdate"));
    globalBestInitializationParameter = new GlobalBestInitializationParameter(args,
        List.of("defaultGlobalBestInitialization"));
    globalBestSelectionParameter = new GlobalBestSelectionParameter(args,
        List.of("binaryTournament", "random"));
    globalBestSelectionParameter = new GlobalBestSelectionParameter(args,
        Arrays.asList("binaryTournament", "random"));
    globalBestUpdateParameter = new GlobalBestUpdateParameter(args,
        Arrays.asList("defaultGlobalBestUpdate"));

    positionUpdateParameter = new PositionUpdateParameter(args,
        Arrays.asList("defaultPositionUpdate"));
    var velocityChangeWhenLowerLimitIsReachedParameter = new RealParameter("velocityChangeWhenLowerLimitIsReached",args, -1.0, 1.0) ;
    var velocityChangeWhenUpperLimitIsReachedParameter = new RealParameter("velocityChangeWhenUpperLimitIsReached",args, -1.0, 1.0) ;
    positionUpdateParameter.addSpecificParameter("defaultPositionUpdate", velocityChangeWhenLowerLimitIsReachedParameter);
    positionUpdateParameter.addSpecificParameter("defaultPositionUpdate", velocityChangeWhenUpperLimitIsReachedParameter);

    perturbationParameter = configurePerturbation(args);

    leaderArchiveParameter = new ExternalArchiveParameter("leaderArchive", args,
        List.of("crowdingDistanceArchive", "hypervolumeArchive", "spatialSpreadDeviationArchive"));

    inertiaWeightComputingParameter = new InertiaWeightComputingParameter(args,
        List.of("constantValue", "randomSelectedValue", "linearIncreasingValue", "linearDecreasingValue"));

    RealParameter weightParameter = new RealParameter("weight", args, 0.1, 1.0);
    RealParameter wMinParameter = new RealParameter("weightMin", args, 0.1, 0.5);
    RealParameter wMaxParameter = new RealParameter("weightMax", args, 0.5, 1.0);
    inertiaWeightComputingParameter.addSpecificParameter("constantValue", weightParameter);
    inertiaWeightComputingParameter.addSpecificParameter("randomSelectedValue", wMinParameter);
    inertiaWeightComputingParameter.addSpecificParameter("randomSelectedValue", wMaxParameter);
    inertiaWeightComputingParameter.addSpecificParameter("linearIncreasingValue", wMinParameter);
    inertiaWeightComputingParameter.addSpecificParameter("linearIncreasingValue", wMaxParameter);
    inertiaWeightComputingParameter.addSpecificParameter("linearDecreasingValue", wMinParameter);
    inertiaWeightComputingParameter.addSpecificParameter("linearDecreasingValue", wMaxParameter);

    autoConfigurableParameterList.add(swarmSizeParameter);
    autoConfigurableParameterList.add(archiveSizeParameter);
    autoConfigurableParameterList.add(leaderArchiveParameter);
    autoConfigurableParameterList.add(swarmInitializationParameter);
    autoConfigurableParameterList.add(velocityInitializationParameter);
    autoConfigurableParameterList.add(perturbationParameter);
    autoConfigurableParameterList.add(inertiaWeightComputingParameter);
    autoConfigurableParameterList.add(velocityUpdateParameter);
    autoConfigurableParameterList.add(localBestInitializationParameter);
    autoConfigurableParameterList.add(globalBestInitializationParameter);
    autoConfigurableParameterList.add(globalBestSelectionParameter);
    autoConfigurableParameterList.add(globalBestUpdateParameter);
    autoConfigurableParameterList.add(localBestUpdateParameter);
    autoConfigurableParameterList.add(positionUpdateParameter);

    for (Parameter<?> parameter : autoConfigurableParameterList) {
      parameter.parse().check();
    }
  }

  private PerturbationParameter configurePerturbation(String[] args) {
    mutationParameter =
        new MutationParameter(args, Arrays.asList("uniform", "polynomial", "nonUniform"));
    //ProbabilityParameter mutationProbability =
    //    new ProbabilityParameter("mutationProbability", args);
    RealParameter mutationProbabilityFactor = new RealParameter("mutationProbabilityFactor", args, 0.0, 2.0) ;
    mutationParameter.addGlobalParameter(mutationProbabilityFactor);
    RepairDoubleSolutionStrategyParameter mutationRepairStrategy =
        new RepairDoubleSolutionStrategyParameter(
            "mutationRepairStrategy", args, Arrays.asList("random", "round", "bounds"));
    mutationParameter.addGlobalParameter(mutationRepairStrategy);

    RealParameter distributionIndexForPolynomialMutation =
        new RealParameter("polynomialMutationDistributionIndex", args, 5.0, 400.0);
    mutationParameter.addSpecificParameter("polynomial", distributionIndexForPolynomialMutation);

    RealParameter uniformMutationPerturbation =
        new RealParameter("uniformMutationPerturbation", args, 0.0, 1.0);
    mutationParameter.addSpecificParameter("uniform", uniformMutationPerturbation);

    RealParameter nonUniformMutationPerturbation =
        new RealParameter("nonUniformMutationPerturbation", args, 0.0, 1.0);
    mutationParameter.addSpecificParameter("nonUniform", nonUniformMutationPerturbation);

    Problem<DoubleSolution> problem = ProblemFactory.loadProblem(problemNameParameter.getValue());
    mutationParameter.addNonConfigurableParameter("numberOfProblemVariables", problem.getNumberOfVariables());

    // TODO: the upper bound  must be the swarm size
    IntegerParameter frequencyOfApplicationParameter = new IntegerParameter(
        "frequencyOfApplicationOfMutationOperator", args, 1, 10);

    perturbationParameter = new PerturbationParameter(args,
        List.of("frequencySelectionMutationBasedPerturbation"));
    perturbationParameter.addSpecificParameter("frequencySelectionMutationBasedPerturbation",
        mutationParameter);
    perturbationParameter.addSpecificParameter("frequencySelectionMutationBasedPerturbation",
        frequencyOfApplicationParameter);

    return perturbationParameter;
  }

  private VelocityUpdateParameter configureVelocityUpdate(String[] args) {
    RealParameter c1MinParameter = new RealParameter("c1Min", args, 1.0, 2.0);
    RealParameter c1MaxParameter = new RealParameter("c1Max", args, 2.0, 3.0);
    RealParameter c2MinParameter = new RealParameter("c2Min", args, 1.0, 2.0);
    RealParameter c2MaxParameter = new RealParameter("c2Max", args, 2.0, 3.0);

    velocityUpdateParameter = new VelocityUpdateParameter(args,
        List.of("defaultVelocityUpdate", "constrainedVelocityUpdate"));
    velocityUpdateParameter.addGlobalParameter(c1MinParameter);
    velocityUpdateParameter.addGlobalParameter(c1MaxParameter);
    velocityUpdateParameter.addGlobalParameter(c2MinParameter);
    velocityUpdateParameter.addGlobalParameter(c2MaxParameter);

    return velocityUpdateParameter;
  }

  protected Problem<DoubleSolution> getProblem() {
    return ProblemFactory.loadProblem(problemNameParameter.getValue());
  }

  /**
   * Create an instance of MOPSO from the parsed parameters
   */
  public ParticleSwarmOptimizationAlgorithm create() {
    Problem<DoubleSolution> problem = getProblem() ;
    int swarmSize = swarmSizeParameter.getValue();
    int maximumNumberOfEvaluations = maximumNumberOfEvaluationsParameter.getValue();

    var swarmInitialization = new RandomSolutionsCreation<>(problem, swarmSize);

    var evaluation = new SequentialEvaluation<>(problem);
    var termination = new TerminationByEvaluations(maximumNumberOfEvaluations);

    leaderArchiveParameter.setSize(archiveSizeParameter.getValue());
    BoundedArchive<DoubleSolution> leaderArchive = (BoundedArchive<DoubleSolution>) leaderArchiveParameter.getParameter();
    var velocityInitialization = new DefaultVelocityInitialization();

    if (velocityUpdateParameter.getValue().equals("constrainedVelocityUpdate")) {
      velocityUpdateParameter.addNonConfigurableParameter("problem", problem);
    }

    if ((inertiaWeightComputingParameter.getValue().equals("linearIncreasingValue") ||
        inertiaWeightComputingParameter.getValue().equals("linearDecreasingValue"))) {
      inertiaWeightComputingParameter.addNonConfigurableParameter("maxIterations",
          maximumNumberOfEvaluationsParameter.getValue() / swarmSizeParameter.getValue());
      inertiaWeightComputingParameter.addNonConfigurableParameter("swarmSize", swarmSizeParameter.getValue());
    }
    InertiaWeightComputingStrategy inertiaWeightComputingStrategy = inertiaWeightComputingParameter.getParameter() ;

    var velocityUpdate = velocityUpdateParameter.getParameter();

    LocalBestInitialization localBestInitialization = localBestInitializationParameter.getParameter();
    GlobalBestInitialization globalBestInitialization = globalBestInitializationParameter.getParameter();
    GlobalBestSelection globalBestSelection = globalBestSelectionParameter.getParameter(
        leaderArchive.getComparator());

    if (mutationParameter.getValue().equals("nonUniform")) {
      mutationParameter.addSpecificParameter("nonUniform", maximumNumberOfEvaluationsParameter);
      mutationParameter.addNonConfigurableParameter("maxIterations",
          maximumNumberOfEvaluationsParameter.getValue() / swarmSizeParameter.getValue());
    }

    var perturbation = perturbationParameter.getParameter();

    if (positionUpdateParameter.getValue().equals("defaultPositionUpdate")) {
      positionUpdateParameter.addNonConfigurableParameter("positionBounds",
          ((DoubleProblem) problem).getVariableBounds());
    }

    PositionUpdate positionUpdate = positionUpdateParameter.getParameter();

    GlobalBestUpdate globalBestUpdate = globalBestUpdateParameter.getParameter();
    LocalBestUpdate localBestUpdate = localBestUpdateParameter.getParameter(
        new DefaultDominanceComparator<DoubleSolution>());

    return new ParticleSwarmOptimizationAlgorithm("OMOPSO",
        swarmInitialization,
        evaluation,
        termination,
        velocityInitialization,
        localBestInitialization,
        globalBestInitialization,
        inertiaWeightComputingStrategy,
        velocityUpdate,
        positionUpdate,
        perturbation,
        globalBestUpdate,
        localBestUpdate,
        globalBestSelection,
        leaderArchive);
  }

  public static void print(List<Parameter<?>> parameterList) {
    parameterList.forEach(System.out::println);
  }
}
