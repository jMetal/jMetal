package org.uma.jmetal.auto.parameterv2;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.auto.parameterv2.param.AlgorithmResult;
import org.uma.jmetal.auto.parameterv2.param.PopulationSize;
import org.uma.jmetal.auto.parameterv2.param.PopulationSizeWithArchive;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.Arrays;

public class NSGAIIWithParameters {
  public EvolutionaryAlgorithm<DoubleSolution> create(String[] args) {
    PopulationSize populationSize = new PopulationSize(args);
    AlgorithmResult algorithmResult = new AlgorithmResult(args);
    PopulationSizeWithArchive populationSizeWithArchive =
        new PopulationSizeWithArchive(args, Arrays.asList(10, 20, 50, 100, 200));

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
    return null;
  }

  public static void main(String[] args) {
    String[] parameters =
        ("--populationSize 100 " + "--algorithmResult population --populationSizeWithArchive 100 ")
            .split("\\s+");

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = new NSGAIIWithParameters().create(parameters);
  }
}
