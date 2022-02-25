package org.uma.jmetal.experimental.auto.algorithm.omopso;

import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.experimental.auto.parameter.*;
import org.uma.jmetal.experimental.auto.parameter.catalogue.*;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization.GlobalBestInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization.impl.DefaultGlobalBestInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestupdate.GlobalBestUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestupdate.impl.DefaultGlobalBestUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestinitialization.LocalBestInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestinitialization.impl.DefaultLocalBestInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestupdate.LocalBestUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestupdate.impl.DefaultLocalBestUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.positionupdate.PositionUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.positionupdate.impl.DefaultPositionUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityinitialization.VelocityInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityinitialization.impl.DefaultVelocityInitialization;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.util.repairsolution.impl.RepairDoubleSolutionWithRandomValue;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.termination.Termination;
import org.uma.jmetal.util.termination.impl.TerminationByEvaluations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class to configure OMOPSO with an argument string using class {@link
 * org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm}
 *
 * @autor Daniel Doblas
 */
public class AutoOMOPSO {
  public List<Parameter<?>> autoConfigurableParameterList = new ArrayList<>();
  public List<Parameter<?>> fixedParameterList = new ArrayList<>();

  private StringParameter problemNameParameter;
  public StringParameter referenceFrontFilename;
  public ExternalArchiveParameter externalArchive;
  public CategoricalParameter velocityInitialization;
  private PositiveIntegerValue maximumNumberOfEvaluationsParameter;
  private PositiveIntegerValue archiveSize;
  private IntegerParameter swarmSize;
  private CreateSwarmInitialSolutions createSwarmInitializationParameter;
  private CategoricalParameter localBestInitializationParameter;
  private CategoricalParameter globalBestInitializationParameter;
  private SelectionParameter globalSelectionParameter;
  private CategoricalParameter perturbationParameter;
  private CategoricalParameter positionUpdateParameter;
  private CategoricalParameter globalBestUpdate;
  private CategoricalParameter localBestUpdate;
  private VelocityUpdateParameterDaniVersion velocityUpdate;
  private RealParameter c1MinParameter;
  private RealParameter c1MaxParameter;
  private RealParameter c2MinParameter;
  private RealParameter c2MaxParameter;
  private RealParameter wMinParameter;
  private RealParameter wMaxParameter;

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

    velocityInitialization =
        new CategoricalParameter(
            "velocityInitialization", args, List.of("defaultVelocityInitialization"));
    localBestInitializationParameter =
        new CategoricalParameter(
            "localBestInitialization", args, List.of("defaultLocalBestInitialiation"));
    externalArchive = new ExternalArchiveParameter(args, List.of("crowdingDistanceArchive"));
    globalBestInitializationParameter =
        new CategoricalParameter(
            "globalBestInitialization", args, List.of("defaultGlobalBestInitialization"));
    swarmSize = new IntegerParameter("swarmSize", args, 2, 200);
    archiveSize = new PositiveIntegerValue("archiveSize", args);
    perturbationParameter =
        new CategoricalParameter("perturbation", args, List.of("mutationBasedPerturbation"));
    positionUpdateParameter =
        new CategoricalParameter("positionUpdate", args, List.of("defaultPositionUpdate"));
    globalBestUpdate =
        new CategoricalParameter("globalBestUpdate", args, List.of("defaultGlobalBestUpdate"));
    localBestUpdate =
        new CategoricalParameter("localBestUpdate", args, List.of("defaultLocalBestUpdate"));
    velocityUpdate =
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

    autoConfigurableParameterList.add(swarmSize);
    autoConfigurableParameterList.add(archiveSize);
    autoConfigurableParameterList.add(createSwarmInitializationParameter);
    autoConfigurableParameterList.add(velocityInitialization);
    autoConfigurableParameterList.add(externalArchive);
    autoConfigurableParameterList.add(localBestInitializationParameter);
    autoConfigurableParameterList.add(globalBestInitializationParameter);
    autoConfigurableParameterList.add(globalSelectionParameter);
    autoConfigurableParameterList.add(perturbationParameter);
    autoConfigurableParameterList.add(positionUpdateParameter);
    autoConfigurableParameterList.add(globalBestUpdate);
    autoConfigurableParameterList.add(localBestUpdate);
    autoConfigurableParameterList.add(velocityUpdate);
    autoConfigurableParameterList.add(c1MaxParameter);
    autoConfigurableParameterList.add(c1MaxParameter);
    autoConfigurableParameterList.add(c2MinParameter);
    autoConfigurableParameterList.add(c2MaxParameter);
    autoConfigurableParameterList.add(wMinParameter);
    autoConfigurableParameterList.add(wMaxParameter);

    for (Parameter<?> parameter : autoConfigurableParameterList) {
      parameter.parse().check();
    }
  }

  private void createSwarmInitialization(String[] args) {
    createSwarmInitializationParameter =
        new CreateSwarmInitialSolutions(
            args, Arrays.asList("random", "latinHypercubeSampling", "scatterSearch"));
  }

  private void selection(String[] args) {
    globalSelectionParameter = new SelectionParameter(args, Arrays.asList("tournament", "random"));
    velocityUpdate.addGlobalParameter(globalSelectionParameter);
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
    perturbationParameter.addSpecificParameter("mutationBasedPerturbation", mutation);
  }

  /** Create an instance of OMOPSO from the parsed parameters */
  public ParticleSwarmOptimizationAlgorithm create() {

    Problem<DoubleSolution> problem = ProblemUtils.loadProblem(problemNameParameter.getValue());

    SolutionsCreation<DoubleSolution> createInitialSolution =
        createSwarmInitializationParameter.getParameter(
            (DoubleProblem) problem, swarmSize.getValue());

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem);

    Termination termination =
        new TerminationByEvaluations(maximumNumberOfEvaluationsParameter.getValue());

    VelocityInitialization velInit = new DefaultVelocityInitialization();

    LocalBestInitialization localBestInit = new DefaultLocalBestInitialization();

    GlobalBestInitialization globalBestInit = new DefaultGlobalBestInitialization();

    double c1Min = c1MinParameter.getValue();
    double c1Max = c1MaxParameter.getValue();
    double c2Min = c2MinParameter.getValue();
    double c2Max = c2MaxParameter.getValue();
    double wMin = wMinParameter.getValue();
    double wMax = wMaxParameter.getValue();

    VelocityUpdateParameterDaniVersion velUpdate =
        (VelocityUpdateParameterDaniVersion)
            velocityUpdate.getParameter(
                (DoubleProblem) problem,
                c1Min,
                c1Max,
                c2Min,
                c2Max,
                wMin,
                wMax,
                0.0,
                0.0,
                0.0,
                0.0);

    PositionUpdate posUpdate =
        new DefaultPositionUpdate(-1.0, -1.0, ((DoubleProblem) problem).getBoundsForVariables());

    GlobalBestUpdate globalBestUpd = new DefaultGlobalBestUpdate();

    LocalBestUpdate localBestUpd = new DefaultLocalBestUpdate(new DominanceComparator<>());

    RepairDoubleSolution mutationSolutionRepair = new RepairDoubleSolutionWithRandomValue();
    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    MutationOperator<DoubleSolution> perturb =
        new PolynomialMutation(
            mutationProbability, mutationDistributionIndex, mutationSolutionRepair);

    Archive<DoubleSolution> archive = null;
    externalArchive.setSize(swarmSize.getValue());
    archive = externalArchive.getParameter();
    swarmSize.setValue(100);

    /*
    var omopso =
        new ParticleSwarmOptimizationAlgorithm(
            "OMOPSO",
            createInitialSolution,
            evaluation,
            termination,
            velInit,
            localBestInit,
            globalBestInit,
            (VelocityUpdate) velUpdate,
            posUpdate,
            (Perturbation) perturb,
            globalBestUpd,
            localBestUpd,
            (GlobalBestSelection) //TODO: Falta global selection
            (BoundedArchive<DoubleSolution>) archive);
*/
    return null;
  }

  public static void print(List<Parameter<?>> parameterList) {
    parameterList.forEach(System.out::println);
  }
}
