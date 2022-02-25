package org.uma.jmetal.experimental.auto.algorithm.mopso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.experimental.auto.algorithm.omopso.CompositeDoubleSolutionMutation;
import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.auto.parameter.IntegerParameter;
import org.uma.jmetal.experimental.auto.parameter.Parameter;
import org.uma.jmetal.experimental.auto.parameter.PositiveIntegerValue;
import org.uma.jmetal.experimental.auto.parameter.RealParameter;
import org.uma.jmetal.experimental.auto.parameter.StringParameter;
import org.uma.jmetal.experimental.auto.parameter.catalogue.CreateInitialSolutionsParameter;
import org.uma.jmetal.experimental.auto.parameter.catalogue.ExternalArchiveParameter;
import org.uma.jmetal.experimental.auto.parameter.catalogue.MutationParameter;
import org.uma.jmetal.experimental.auto.parameter.catalogue.PerturbationParameter;
import org.uma.jmetal.experimental.auto.parameter.catalogue.ProbabilityParameter;
import org.uma.jmetal.experimental.auto.parameter.catalogue.RepairDoubleSolutionStrategyParameter;
import org.uma.jmetal.experimental.auto.parameter.catalogue.SelectionParameter;
import org.uma.jmetal.experimental.auto.parameter.catalogue.VelocityUpdateParameter;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization.impl.DefaultGlobalBestInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestselection.impl.TournamentGlobalBestSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestupdate.impl.DefaultGlobalBestUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestinitialization.impl.DefaultLocalBestInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestupdate.impl.DefaultLocalBestUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.perturbation.Perturbation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.positionupdate.impl.DefaultPositionUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityinitialization.impl.DefaultVelocityInitialization;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.NonUniformMutation;
import org.uma.jmetal.operator.mutation.impl.NullMutation;
import org.uma.jmetal.operator.mutation.impl.UniformMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.termination.impl.TerminationByEvaluations;

/**
 * Class to configure OMOPSO with an argument string using class {@link
 * ParticleSwarmOptimizationAlgorithm}
 *
 * @autor Daniel Doblas
 */
public class AutoMOPSO {

  public List<Parameter<?>> autoConfigurableParameterList = new ArrayList<>();
  public List<Parameter<?>> fixedParameterList = new ArrayList<>();

  private StringParameter problemNameParameter;
  public StringParameter referenceFrontFilenameParameter;
  public ExternalArchiveParameter externalArchiveParameter;
  public CategoricalParameter velocityInitializationParameter;
  private PositiveIntegerValue maximumNumberOfEvaluationsParameter;
  private PositiveIntegerValue archiveSizeParameter;
  private IntegerParameter swarmSizeParameter;
  private CreateInitialSolutionsParameter swarmInitializationParameter;
  private CategoricalParameter localBestInitializationParameter;
  private CategoricalParameter globalBestInitializationParameter;
  private SelectionParameter globalSelectionParameter;
  private PerturbationParameter perturbationParameter;
  private CategoricalParameter positionUpdateParameter;
  private CategoricalParameter globalBestUpdateParameter;
  private CategoricalParameter localBestUpdateParameter;
  private VelocityUpdateParameter velocityUpdateParameter;
  private RealParameter c1MinParameter;
  private RealParameter c1MaxParameter;
  private RealParameter c2MinParameter;
  private RealParameter c2MaxParameter;
  private RealParameter wMinParameter;
  private RealParameter wMaxParameter;

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

    swarmSizeParameter = new IntegerParameter("swarmSize", args, 2, 200);
    archiveSizeParameter = new PositiveIntegerValue("archiveSize", args);

    swarmInitializationParameter =
        new CreateInitialSolutionsParameter("swarmInitialization",
            args, Arrays.asList("random", "latinHypercubeSampling", "scatterSearch"));
    //createSwarm(args); ;

    velocityInitializationParameter =
        new CategoricalParameter(
            "velocityInitialization", args, List.of("defaultVelocityInitialization"));

    velocityUpdate(args);
/*
    velocityInitializationParameter =
        new CategoricalParameter(
            "velocityInitialization", args, List.of("defaultVelocityInitialization"));
    localBestInitializationParameter =
        new CategoricalParameter(
            "localBestInitialization", args, List.of("defaultLocalBestInitialiation"));
    externalArchiveParameter = new ExternalArchiveParameter(args, List.of("crowdingDistanceArchive"));
    globalBestInitializationParameter =
        new CategoricalParameter(
            "globalBestInitialization", args, List.of("defaultGlobalBestInitialization"));
    perturbationParameter =
        new CategoricalParameter("perturbation", args, List.of("mutationBasedPerturbation"));
    positionUpdateParameter =
        new CategoricalParameter("positionUpdate", args, List.of("defaultPositionUpdate"));
    globalBestUpdateParameter =
        new CategoricalParameter("globalBestUpdate", args, List.of("defaultGlobalBestUpdate"));
    localBestUpdateParameter =
        new CategoricalParameter("localBestUpdate", args, List.of("defaultLocalBestUpdate"));
    velocityUpdateParameter =
        new VelocityUpdateParameterDaniVersion(
            args, List.of("defaultVelocityUpdate", "constrainedVelocityUpdate"));
    c1MinParameter = new RealParameter("c1Min", args, 1.0, 2.0);
    c1MaxParameter = new RealParameter("c1Max", args, 2.0, 3.0);
    c2MinParameter = new RealParameter("c2Min", args, 1.0, 2.0);
    c2MaxParameter = new RealParameter("c2Max", args, 2.0, 3.0);
    wMinParameter = new RealParameter("wMin", args, 0.1, 0.5);
    wMaxParameter = new RealParameter("wMax", args, 0.5, 1.0);

    createSwarmInitialization(args);
    selection(args);
    perturbation(args);

    autoConfigurableParameterList.add(swarmSizeParameter);
    autoConfigurableParameterList.add(archiveSizeParameter);
    autoConfigurableParameterList.add(createSwarmInitializationParameter);
    autoConfigurableParameterList.add(velocityInitializationParameter);
    autoConfigurableParameterList.add(externalArchiveParameter);
    autoConfigurableParameterList.add(localBestInitializationParameter);
    autoConfigurableParameterList.add(globalBestInitializationParameter);
    autoConfigurableParameterList.add(globalSelectionParameter);
    autoConfigurableParameterList.add(perturbationParameter);
    autoConfigurableParameterList.add(positionUpdateParameter);
    autoConfigurableParameterList.add(globalBestUpdateParameter);
    autoConfigurableParameterList.add(localBestUpdateParameter);
    autoConfigurableParameterList.add(velocityUpdateParameter);
    autoConfigurableParameterList.add(c1MaxParameter);
    autoConfigurableParameterList.add(c1MaxParameter);
    autoConfigurableParameterList.add(c2MinParameter);
    autoConfigurableParameterList.add(c2MaxParameter);
    autoConfigurableParameterList.add(wMinParameter);
    autoConfigurableParameterList.add(wMaxParameter);
*/

    externalArchiveParameter = new ExternalArchiveParameter(args,
        List.of("crowdingDistanceArchive"));
    perturbation(args);

    autoConfigurableParameterList.add(swarmSizeParameter);
    autoConfigurableParameterList.add(archiveSizeParameter);
    autoConfigurableParameterList.add(externalArchiveParameter);
    autoConfigurableParameterList.add(swarmInitializationParameter);
    autoConfigurableParameterList.add(velocityInitializationParameter);
    autoConfigurableParameterList.add(perturbationParameter);
    autoConfigurableParameterList.add(velocityUpdateParameter) ;
    for (Parameter<?> parameter : autoConfigurableParameterList) {
      parameter.parse().check();
    }
  }

  private void selection(String[] args) {
    globalSelectionParameter = new SelectionParameter(args, Arrays.asList("tournament", "random"));
    velocityUpdateParameter.addGlobalParameter(globalSelectionParameter);
    IntegerParameter selectionTournamentSize =
        new IntegerParameter("selectionTournamentSize", args, 2, 10);
    globalSelectionParameter.addSpecificParameter("tournament", selectionTournamentSize);
  }

  private void perturbation(String[] args) {
    MutationParameter mutation =
        new MutationParameter(args, Arrays.asList("uniform", "polynomial", "nonUniform"));
    ProbabilityParameter mutationProbability =
        new ProbabilityParameter("mutationProbability", args);
    mutation.addGlobalParameter(mutationProbability);
    RepairDoubleSolutionStrategyParameter mutationRepairStrategy =
        new RepairDoubleSolutionStrategyParameter(
            "mutationRepairStrategy", args, Arrays.asList("random", "round", "bounds"));
    mutation.addGlobalParameter(mutationRepairStrategy);

    RealParameter distributionIndexForPolynomialMutation =
        new RealParameter("polynomialMutationDistributionIndex", args, 5.0, 400.0);
    mutation.addSpecificParameter("polynomial", distributionIndexForPolynomialMutation);

    RealParameter uniformMutationPerturbation =
        new RealParameter("uniformMutationPerturbation", args, 0.0, 1.0);
    mutation.addSpecificParameter("uniform", uniformMutationPerturbation);

    RealParameter nonUniformMutationPerturbation =
        new RealParameter("nonUniformMutationPerturbation", args, 0.0, 1.0);
    mutation.addSpecificParameter("nonUniform", nonUniformMutationPerturbation);

    // TODO: the upper bound  must be the swarm size
    IntegerParameter frequencyOfApplicationParameter = new IntegerParameter(
        "frequencyOfApplicationOfMutationOperator", args, 1, 100);

    perturbationParameter = new PerturbationParameter(args, List.of("frequencySelectionMutationBasedPerturbation"));
    perturbationParameter.addSpecificParameter("frequencySelectionMutationBasedPerturbation", mutation);
    perturbationParameter.addSpecificParameter("frequencySelectionMutationBasedPerturbation",
        frequencyOfApplicationParameter);
  }

  private void velocityUpdate(String[] args) {
    c1MinParameter = new RealParameter("c1Min", args, 1.0, 2.0);
    c1MaxParameter = new RealParameter("c1Max", args, 2.0, 3.0);
    c2MinParameter = new RealParameter("c2Min", args, 1.0, 2.0);
    c2MaxParameter = new RealParameter("c2Max", args, 2.0, 3.0);
    wMinParameter = new RealParameter("wMin", args, 0.1, 0.5);
    wMaxParameter = new RealParameter("wMax", args, 0.5, 1.0);

    velocityUpdateParameter = new VelocityUpdateParameter(args, List.of("defaultVelocityUpdate")) ;
    velocityUpdateParameter.addGlobalParameter(c1MinParameter);
    velocityUpdateParameter.addGlobalParameter(c1MaxParameter);
    velocityUpdateParameter.addGlobalParameter(c2MinParameter);
    velocityUpdateParameter.addGlobalParameter(c2MaxParameter);
    velocityUpdateParameter.addGlobalParameter(wMinParameter);
    velocityUpdateParameter.addGlobalParameter(wMaxParameter);
  }

  /**
   * Create an instance of MOPSO from the parsed parameters
   */
  public ParticleSwarmOptimizationAlgorithm create() {
    Problem<DoubleSolution> problem = ProblemUtils.loadProblem(problemNameParameter.getValue());
    int swarmSize = swarmSizeParameter.getValue();
    int maximumNumberOfEvaluations = maximumNumberOfEvaluationsParameter.getValue();
    String referenceFrontFileName = referenceFrontFilenameParameter.getValue();

    var swarmInitialization = new RandomSolutionsCreation<>(problem, swarmSize);

    var evaluation = new SequentialEvaluation<>(problem);
    var termination = new TerminationByEvaluations(maximumNumberOfEvaluations);

    externalArchiveParameter.setSize(archiveSizeParameter.getValue());
    BoundedArchive<DoubleSolution> externalArchive = (BoundedArchive<DoubleSolution>) externalArchiveParameter.getParameter();
    var velocityInitialization = new DefaultVelocityInitialization();

    var velocityUpdate = velocityUpdateParameter.getParameter() ;

    ///////// TO IMPLEMENT USING PARAMETERS
    var localBestInitialization = new DefaultLocalBestInitialization();
    var globalBestInitialization = new DefaultGlobalBestInitialization();

    var globalBestSelection = new TournamentGlobalBestSelection(2, externalArchive.getComparator());

    ArrayList<MutationOperator<DoubleSolution>> operators = new ArrayList<>();
    operators.add(new UniformMutation(1.0 / problem.getNumberOfVariables(), 0.5));
    operators.add(new NonUniformMutation(1.0 / problem.getNumberOfVariables(), 0.5, 250));
    operators.add(new NullMutation<>());
    CompositeDoubleSolutionMutation mutation = new CompositeDoubleSolutionMutation(operators);

    double velocityChangeWhenLowerLimitIsReached = -1.0;
    double velocityChangeWhenUpperLimitIsReached = -1.0;
    var positionUpdate = new DefaultPositionUpdate(velocityChangeWhenLowerLimitIsReached,
        velocityChangeWhenUpperLimitIsReached, ((DoubleProblem) problem).getBoundsForVariables());
    Perturbation perturbation = perturbationParameter.getParameter() ;

    //var perturbation = new MutationBasedPerturbation(mutation);
    var globalBestUpdate = new DefaultGlobalBestUpdate();
    var localBestUpdate = new DefaultLocalBestUpdate(new DominanceComparator<>());

    ////
    var mopso = new ParticleSwarmOptimizationAlgorithm("OMOPSO",
        swarmInitialization,
        evaluation,
        termination,
        velocityInitialization,
        localBestInitialization,
        globalBestInitialization,
        velocityUpdate,
        positionUpdate,
        perturbation,
        globalBestUpdate,
        localBestUpdate,
        globalBestSelection,
        externalArchive);
    return mopso;
  }

  public static void print(List<Parameter<?>> parameterList) {
    parameterList.forEach(System.out::println);
  }
}
