package org.uma.jmetal.auto.parameterv2;

import java_cup.parser;
import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.auto.algorithm.nsgaiib.MissingParameterException;
import org.uma.jmetal.auto.algorithm.nsgaiib.Parameter;
import org.uma.jmetal.auto.irace.parametertype.ParameterType;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class NSGAIIWithParameters {
  public EvolutionaryAlgorithm<DoubleSolution> create(String[] args) {
    PopulationSize populationSize = new PopulationSize(args);
    AlgorithmResult algorithmResult = new AlgorithmResult(args);

    System.out.println("Population size: " + populationSize.getValue());
    System.out.println(
        "Algorithm result: "
            + algorithmResult.getValue()
            + ". Valid values: "
            + algorithmResult.getValidValues());
    return null;
  }

  public static void main(String[] args) {
    String[] parameters = ("--populationSize 100 " + "--algorithmResult population").split(" ");

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = new NSGAIIWithParameters().create(parameters);
  }
}
