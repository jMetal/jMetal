package org.uma.jmetal.auto.parameterv2;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.auto.parameterv2.param.Parameter;
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

    /*
    System.out.println("Population size: " + populationSize.getValue());
    System.out.println(
        "Algorithm result: "
            + algorithmResult.getValue()
            + ". Valid values: "
            + algorithmResult.getValidValues());
    System.out.println(
        "Population size when an archive is used: "
            + populationSizeWithArchive.getValue()
            + ". Valid values: "
            + populationSizeWithArchive.getValidValues());

    System.out.println(
        "Offspring population size: "
            + offspringPopulationSize.getValue()
            + ". Valid values: "
            + offspringPopulationSize.getValidValues());

    System.out.println(
        "Create initial solutions strategies: "
            + createInitialSolutions.getValue()
            + ". Valid values: "
            + createInitialSolutions.getValidValues());

    System.out.println(
        "Variation: "
            + variation.getValue()
            + ". Valid values: "
            + variation.getValidValues());

    System.out.println(
        "Selection: "
            + selection.getValue()
            + ". Valid values: "
            + selection.getValidValues()
            + selection.getSpecificParameters());
    */
    nsgaIIParameters.put(populationSize.getName(), populationSize);
    nsgaIIParameters.put(algorithmResult.getName(), algorithmResult);
    nsgaIIParameters.put(populationSizeWithArchive.getName(), populationSizeWithArchive);
    nsgaIIParameters.put(offspringPopulationSize.getName(), offspringPopulationSize);
    nsgaIIParameters.put(createInitialSolutions.getName(), createInitialSolutions);
    nsgaIIParameters.put(variation.getName(), variation) ;
    nsgaIIParameters.put(selection.getName(), selection) ;

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
            "--selectionTournamentSize 4")
            .split("\\s+");

    NSGAIIWithParameters nsgaiiWithParameters = new NSGAIIWithParameters();

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = nsgaiiWithParameters.create(parameters);

    NSGAIIiraceParameterFile nsgaiiiraceParameterFile = new NSGAIIiraceParameterFile();
    nsgaiiiraceParameterFile.create(nsgaiiWithParameters.nsgaIIParameters);
  }
}
