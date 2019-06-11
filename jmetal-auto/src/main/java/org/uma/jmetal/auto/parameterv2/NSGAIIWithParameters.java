package org.uma.jmetal.auto.parameterv2;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.auto.parameterv2.param.Parameter;
import org.uma.jmetal.auto.parameterv2.param.RealParameter;
import org.uma.jmetal.auto.parameterv2.param.catalogue.*;
import org.uma.jmetal.auto.parameterv2.param.irace.NSGAIIiraceParameterFile;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class NSGAIIWithParameters {
  public Map<String, Parameter<?>> nsgaIIParameters = new HashMap<>();

  public EvolutionaryAlgorithm<DoubleSolution> create(String[] args) {
    PopulationSize populationSize = new PopulationSize(args);
    AlgorithmResult algorithmResult = new AlgorithmResult(args);
    PopulationSizeWithArchive populationSizeWithArchive =
        new PopulationSizeWithArchive(args, Arrays.asList(10, 20, 50, 100, 200));
    OffspringPopulationSize offspringPopulationSize =
        new OffspringPopulationSize(args, Arrays.asList(1, 10, 50, 100));

    CreateInitialSolutions createInitialSolutions = new CreateInitialSolutions(args);
    Variation variation = new Variation(args) ;
    Selection selection = new Selection(args, Arrays.asList("tournament", "random")) ;
    if (selection.getValue().equals("tournament")) {
      SelectionTournamentSize selectionTournamentSize = new SelectionTournamentSize(args,2, 10) ;
      selection.addSpecificParameter(selectionTournamentSize);
    }

    Crossover crossover = new Crossover(args) ;
    Probability crossoverProbability = new Probability(args, "crossoverProbability") ;
    crossover.addGlobalParameter(crossoverProbability);
    RepairStrategy crossoverRepairStrategy = new RepairStrategy(args, "crossoverRepairStrategy", Arrays.asList("random", "round", "bounds")) ;
    crossover.addGlobalParameter(crossoverRepairStrategy);

    if (crossover.getValue().equals("SBX")) {
      RealParameter distributionIndex = new DistributionIndex(args, "sbxDistributionIndex", 5.0, 400.0) ;
      crossover.addSpecificParameter(distributionIndex);
    }

    if (crossover.getValue().equals("BLX_ALPHA")) {
      RealValueInRange alpha = new RealValueInRange(args, "blxAlphaCrossoverAlphaValue", 0.0, 1.0) ;
      crossover.addSpecificParameter(alpha);
    }

    nsgaIIParameters.put(populationSize.getName(), populationSize);
    nsgaIIParameters.put(algorithmResult.getName(), algorithmResult);
    nsgaIIParameters.put(populationSizeWithArchive.getName(), populationSizeWithArchive);
    nsgaIIParameters.put(offspringPopulationSize.getName(), offspringPopulationSize);
    nsgaIIParameters.put(createInitialSolutions.getName(), createInitialSolutions);
    nsgaIIParameters.put(variation.getName(), variation) ;
    nsgaIIParameters.put(selection.getName(), selection) ;
    nsgaIIParameters.put(crossover.getName(), crossover) ;

    print(nsgaIIParameters) ;
    System.out.println() ;

    return null;
  }

  public static void print(Map<String, Parameter<?>> parameterMap) {
    parameterMap.forEach((key, value) -> System.out.println(value.toString()));
  }

  public static void main(String[] args) {
    String[] parameters =
        ("--populationSize 100 " +
            "--algorithmResult population " +
            "--populationSizeWithArchive 100 " +
            "--offspringPopulationSize 100 " +
            "--createInitialSolutions random " +
            "--variation crossoverAndMutationVariation " +
            "--selection tournament " +
            "--selectionTournamentSize 4 " +
            "--crossover SBX " +
            "--crossoverProbability 0.9 " +
            "--crossoverRepairStrategy bounds " +
            "--sbxDistributionIndex 20.0 ")
            .split("\\s+");

    NSGAIIWithParameters nsgaiiWithParameters = new NSGAIIWithParameters();

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = nsgaiiWithParameters.create(parameters);

    NSGAIIiraceParameterFile nsgaiiiraceParameterFile = new NSGAIIiraceParameterFile();
    nsgaiiiraceParameterFile.create(nsgaiiWithParameters.nsgaIIParameters);
  }
}
