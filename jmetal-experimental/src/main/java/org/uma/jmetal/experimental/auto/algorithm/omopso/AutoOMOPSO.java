package org.uma.jmetal.experimental.auto.algorithm.omopso;

import org.checkerframework.checker.units.qual.C;
import org.uma.jmetal.experimental.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.experimental.auto.parameter.*;
import org.uma.jmetal.experimental.auto.parameter.catalogue.*;

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
  private CategoricalParameter velocityUpdate;
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
            new CategoricalParameter("velocityInitialization", args ,List.of("defaultVelocityInitialization"));
    localBestInitializationParameter = new CategoricalParameter("localBestInitialization", args, List.of("defaultLocalBestInitialiation"));
    externalArchive = new ExternalArchiveParameter(args, List.of("crowdingDistanceArchive")) ;
    globalBestInitializationParameter = new CategoricalParameter("globalBestInitialization", args, List.of("defaultGlobalBestInitialization"));
    swarmSize = new IntegerParameter("swarmSize", args, 2, 200);
    archiveSize = new PositiveIntegerValue("archiveSize", args);
    perturbationParameter = new CategoricalParameter("perturbation", args, List.of("mutationBasedPerturbation"));
    positionUpdateParameter = new CategoricalParameter("positionUpdate", args, List.of("defaultPositionUpdate"));
    globalBestUpdate = new CategoricalParameter("globalBestUpdate", args, List.of("defaultGlobalBestUpdate"));
    localBestUpdate = new CategoricalParameter("localBestUpdate", args, List.of("defaultLocalBestUpdate"));
    velocityUpdate = new CategoricalParameter("velocityUpdate", args, List.of("defaultVelocityUpdate","constrainedVelocityUpdate"));
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
    IntegerParameter selectionTournamentSize =
            new IntegerParameter("selectionTournamentSize", args, 2, 10);
    globalSelectionParameter.addSpecificParameter("tournament", selectionTournamentSize);
  }

  private void perturbation(String[] args){
    MutationParameter mutation = new MutationParameter(args, Arrays.asList("uniform", "polynomial", "nonUniform"));
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
    perturbationParameter.addSpecificParameter("crossoverAndMutationVariation", mutation);
  }

  public static void print(List<Parameter<?>> parameterList) {
    parameterList.forEach(System.out::println);
  }
}
