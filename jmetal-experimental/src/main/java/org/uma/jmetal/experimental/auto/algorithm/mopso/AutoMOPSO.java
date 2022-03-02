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
import org.uma.jmetal.experimental.auto.parameter.catalogue.*;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization.GlobalBestInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestselection.GlobalBestSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestselection.impl.BinaryTournamentGlobalBestSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestselection.impl.TournamentGlobalBestSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestupdate.GlobalBestUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestupdate.impl.DefaultGlobalBestUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestinitialization.LocalBestInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestupdate.LocalBestUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestupdate.impl.DefaultLocalBestUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.perturbation.Perturbation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.positionupdate.PositionUpdate;
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
  private LocalBestInitializationParameter localBestInitializationParameter;
  private GlobalBestInitializationParameter globalBestInitializationParameter;
  private GlobalBestSelectionParameter globalBestSelectionParameter ;
  private PerturbationParameter perturbationParameter;
  private PositionUpdateParameter positionUpdateParameter;
  private GlobalBestUpdateParameter globalBestUpdateParameter;
  private LocalBestUpdateParameter localBestUpdateParameter;
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

    velocityUpdateParameter = configureVelocityUpdate(args);

    localBestInitializationParameter = new LocalBestInitializationParameter(args, List.of("defaultLocalBestInitialization")) ;
    localBestUpdateParameter = new LocalBestUpdateParameter(args, Arrays.asList("defaultLocalBestUpdate"));
    globalBestInitializationParameter = new GlobalBestInitializationParameter(args, List.of("defaultGlobalBestInitialization")) ;
    globalBestSelectionParameter = new GlobalBestSelectionParameter(args, List.of("binaryTournament", "random")) ;
    globalBestSelectionParameter = new GlobalBestSelectionParameter(args, Arrays.asList("binaryTournament", "random"));
    globalBestUpdateParameter = new GlobalBestUpdateParameter(args, Arrays.asList("defaultGlobalBestUpdate"));

    positionUpdateParameter = new PositionUpdateParameter(args, Arrays.asList("defaultPositionUpdate"));

    perturbationParameter = configurePerturbation(args) ;

    externalArchiveParameter = new ExternalArchiveParameter(args,
        List.of("crowdingDistanceArchive"));

    autoConfigurableParameterList.add(swarmSizeParameter);
    autoConfigurableParameterList.add(archiveSizeParameter);
    autoConfigurableParameterList.add(externalArchiveParameter);
    autoConfigurableParameterList.add(swarmInitializationParameter);
    autoConfigurableParameterList.add(velocityInitializationParameter);
    autoConfigurableParameterList.add(perturbationParameter);
    autoConfigurableParameterList.add(velocityUpdateParameter) ;
    autoConfigurableParameterList.add(localBestInitializationParameter) ;
    autoConfigurableParameterList.add(globalBestInitializationParameter) ;
    autoConfigurableParameterList.add(globalBestSelectionParameter) ;
    autoConfigurableParameterList.add(perturbationParameter) ;
    autoConfigurableParameterList.add(globalBestUpdateParameter);
    autoConfigurableParameterList.add(localBestUpdateParameter);
    autoConfigurableParameterList.add(positionUpdateParameter);

    for (Parameter<?> parameter : autoConfigurableParameterList) {
      parameter.parse().check();
    }
  }

  private PerturbationParameter configurePerturbation(String[] args) {
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

    return perturbationParameter ;
  }

  private VelocityUpdateParameter configureVelocityUpdate(String[] args) {
    c1MinParameter = new RealParameter("c1Min", args, 1.0, 2.0);
    c1MaxParameter = new RealParameter("c1Max", args, 2.0, 3.0);
    c2MinParameter = new RealParameter("c2Min", args, 1.0, 2.0);
    c2MaxParameter = new RealParameter("c2Max", args, 2.0, 3.0);
    wMinParameter = new RealParameter("wMin", args, 0.1, 0.5);
    wMaxParameter = new RealParameter("wMax", args, 0.5, 1.0);

    velocityUpdateParameter = new VelocityUpdateParameter(args, List.of("defaultVelocityUpdate", "constrainedVelocityUpdate")) ;
    velocityUpdateParameter.addGlobalParameter(c1MinParameter);
    velocityUpdateParameter.addGlobalParameter(c1MaxParameter);
    velocityUpdateParameter.addGlobalParameter(c2MinParameter);
    velocityUpdateParameter.addGlobalParameter(c2MaxParameter);
    velocityUpdateParameter.addGlobalParameter(wMinParameter);
    velocityUpdateParameter.addGlobalParameter(wMaxParameter);

    return velocityUpdateParameter ;
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

    if (velocityUpdateParameter.getValue().equals("constrainedVelocityUpdate")) {
      velocityUpdateParameter.addNonConfigurableParameter("problem", problem);
    };
    var velocityUpdate = velocityUpdateParameter.getParameter() ;

    LocalBestInitialization localBestInitialization = localBestInitializationParameter.getParameter() ;
    GlobalBestInitialization globalBestInitialization = globalBestInitializationParameter.getParameter() ;
    GlobalBestSelection globalBestSelection = globalBestSelectionParameter.getParameter(externalArchive.getComparator());
    Perturbation perturbation = perturbationParameter.getParameter() ;

    ///////// TO IMPLEMENT USING PARAMETERS
    if(positionUpdateParameter.getValue().equals("defaultPositionUpdate")){
      positionUpdateParameter.addNonConfigurableParameter("positionBounds", ((DoubleProblem) problem).getBoundsForVariables());
      positionUpdateParameter.addNonConfigurableParameter("velocityChangeWhenLowerLimitIsReached", -1.0);
      positionUpdateParameter.addNonConfigurableParameter("velocityChangeWhenUpperLimitIsReached", -1.0);
    }

    PositionUpdate positionUpdate = positionUpdateParameter.getParameter();

    GlobalBestUpdate globalBestUpdate = globalBestUpdateParameter.getParameter();
    LocalBestUpdate localBestUpdate = localBestUpdateParameter.getParameter(new DominanceComparator());

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
