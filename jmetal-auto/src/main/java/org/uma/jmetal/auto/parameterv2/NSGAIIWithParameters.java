package org.uma.jmetal.auto.parameterv2;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.auto.parameterv2.param.Parameter;
import org.uma.jmetal.auto.parameterv2.param.RealParameter;
import org.uma.jmetal.auto.parameterv2.param.catalogue.*;
import org.uma.jmetal.auto.parameterv2.param.irace.NSGAIIiraceParameterFile;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.*;

public class NSGAIIWithParameters {
  public List<Parameter<?>> configurableParameterList = new ArrayList<>();
  public Map<String, Parameter<?>> parameterMap = new HashMap<>() ;

  public void parseParameters(String[] args) {
    AlgorithmResult algorithmResult =
        new AlgorithmResult(args, Arrays.asList("externalArchive", "population"));
    PopulationSize populationSize = new PopulationSize(args);
    algorithmResult.addSpecificParameter("population", populationSize);
    PopulationSizeWithArchive populationSizeWithArchive =
        new PopulationSizeWithArchive(args, Arrays.asList(10, 20, 50, 100, 200));
    algorithmResult.addSpecificParameter("externalArchive", populationSizeWithArchive);

    OffspringPopulationSize offspringPopulationSize =
        new OffspringPopulationSize(args, Arrays.asList(1, 10, 50, 100));

    CreateInitialSolutions createInitialSolutions =
        new CreateInitialSolutions(
            args, Arrays.asList("random", "latinHypercubeSampling", "scatterSearch"));

    Selection selection = new Selection(args, Arrays.asList("tournament", "random"));
    SelectionTournamentSize selectionTournamentSize = new SelectionTournamentSize(args, 2, 10);
    selection.addSpecificParameter("tournament", selectionTournamentSize);

    Crossover crossover = new Crossover(args, Arrays.asList("SBX", "BLX_ALPHA"));
    Probability crossoverProbability = new Probability(args, "crossoverProbability");
    crossover.addGlobalParameter(crossoverProbability);
    RepairStrategy crossoverRepairStrategy =
        new RepairStrategy(
            args, "crossoverRepairStrategy", Arrays.asList("random", "round", "bounds"));
    crossover.addGlobalParameter(crossoverRepairStrategy);

    RealParameter distributionIndex =
        new DistributionIndex(args, "sbxDistributionIndex", 5.0, 400.0);
    crossover.addSpecificParameter("SBX", distributionIndex);

    RealValueInRange alpha = new RealValueInRange(args, "blxAlphaCrossoverAlphaValue", 0.0, 1.0);
    crossover.addSpecificParameter("BLX_ALPHA", alpha);

    Mutation mutation = new Mutation(args, Arrays.asList("uniform", "polynomial"));
    Probability mutationProbability = new Probability(args, "mutationProbability");
    mutation.addGlobalParameter(mutationProbability);
    RepairStrategy mutationRepairStrategy =
        new RepairStrategy(
            args, "mutationRepairStrategy", Arrays.asList("random", "round", "bounds"));
    mutation.addGlobalParameter(mutationRepairStrategy);

    RealParameter distributionIndexForMutation =
        new DistributionIndex(args, "polynomialMutationDistributionIndex", 5.0, 400.0);
    mutation.addSpecificParameter("polynomial", distributionIndexForMutation);

    RealParameter uniformMutationPerturbation =
        new RealValueInRange(args, "uniformMutationPerturbation", 0.0, 1.0);
    mutation.addSpecificParameter("uniform", uniformMutationPerturbation);

    Variation variation = new Variation(args, Arrays.asList("crossoverAndMutationVariation"));
    variation.addGlobalParameter(crossover);
    variation.addGlobalParameter(mutation);

    configurableParameterList.add(algorithmResult);
    configurableParameterList.add(offspringPopulationSize);
    configurableParameterList.add(createInitialSolutions);
    configurableParameterList.add(variation);
    configurableParameterList.add(selection);

    for (Parameter<?> parameter : configurableParameterList) {
      parameter.parse().check();
    }

    parameterMap.put(algorithmResult.getName(), algorithmResult) ;
    parameterMap.put(populationSize.getName(), populationSize) ;
    parameterMap.put(populationSizeWithArchive.getName(), populationSizeWithArchive) ;
  }

  EvolutionaryAlgorithm<DoubleSolution> create(String[] args, List<Parameter<?>> parameterList) {
    return null;
  }

  public static void print(List<Parameter<?>> parameterList) {
    parameterList.forEach(item -> System.out.println(item));
  }

  public static void main(String[] args) {
    String[] parameters =
        ("--populationSize 100 "
                + "--problemName org.uma.jmetal.problem.multiobjective.zdt.ZDT1 "
                + "--referenceFront ZDT1.pf "
                + "--maximumNumberOfIterations 25000 "
                + "--algorithmResult population "
                + "--populationSize 100 "
                + "--offspringPopulationSize 100 "
                + "--createInitialSolutions random "
                + "--variation crossoverAndMutationVariation "
                + "--selection tournament "
                + "--selectionTournamentSize 4 "
                + "--crossover SBX "
                + "--crossoverProbability 0.9 "
                + "--crossoverRepairStrategy bounds "
                + "--sbxDistributionIndex 20.0 "
                + "--mutation polynomial "
                + "--mutationProbability 0.01 "
                + "--mutationRepairStrategy bounds "
                + "--polynomialMutationDistributionIndex 20.0 ")
            .split("\\s+");

    NSGAIIWithParameters nsgaiiWithParameters = new NSGAIIWithParameters();
    nsgaiiWithParameters.parseParameters(parameters);
    List<Parameter<?>> parameterList = nsgaiiWithParameters.configurableParameterList;
    nsgaiiWithParameters.print(parameterList);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = nsgaiiWithParameters.create(args, parameterList);

    NSGAIIiraceParameterFile nsgaiiiraceParameterFile = new NSGAIIiraceParameterFile();
    nsgaiiiraceParameterFile.generateConfigurationFile(parameterList);
  }
}
