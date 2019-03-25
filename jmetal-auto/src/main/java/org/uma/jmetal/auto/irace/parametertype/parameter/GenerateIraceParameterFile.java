package org.uma.jmetal.auto.irace.parametertype.parameter;

import org.uma.jmetal.auto.irace.parametertype.ParameterType;
import org.uma.jmetal.auto.irace.parametertype.impl.CategoricalParameterType;
import org.uma.jmetal.auto.irace.parametertype.impl.IntegerParameterType;
import org.uma.jmetal.auto.irace.parametertype.impl.OrdinalParameterType;
import org.uma.jmetal.auto.irace.parametertype.impl.RealParameterType;
import org.uma.jmetal.auto.irace.parametertype.parameter.crossover.BLXAlphaCrossoverAlphaValueParameter;
import org.uma.jmetal.auto.irace.parametertype.parameter.crossover.CrossoverParameter;
import org.uma.jmetal.auto.irace.parametertype.parameter.crossover.SBXCrossoverDistributionIndexParameter;
import org.uma.jmetal.auto.irace.parametertype.parameter.mutation.MutationParameter;
import org.uma.jmetal.auto.irace.parametertype.parameter.mutation.PolynomialMutationDistributionIndexParameter;
import org.uma.jmetal.auto.irace.parametertype.parameter.mutation.UniformMutationPerturbationParameter;
import org.uma.jmetal.auto.irace.parametertype.parameter.selection.NarityTournamentNParameter;
import org.uma.jmetal.auto.irace.parametertype.parameter.selection.SelectionParameter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GenerateIraceParameterFile {
  public static void main(String[] args) throws IOException {
    List<ParameterType> parameters = new ArrayList<>();

    CategoricalParameterType algorithmResult = new CategoricalParameterType("algorithmResult");
    algorithmResult.addValue("population");
    algorithmResult.addValue("externalArchive");

    CategoricalParameterType populationSize = new CategoricalParameterType("populationSize");
    populationSize.addValue("100");
    populationSize.setParentTag("population");

    OrdinalParameterType populationSizeWithArchive =
        new OrdinalParameterType("populationSizeWithArchive");
    populationSizeWithArchive.addValue("10");
    populationSizeWithArchive.addValue("20");
    populationSizeWithArchive.addValue("50");
    populationSizeWithArchive.addValue("100");
    populationSizeWithArchive.addValue("200");
    populationSizeWithArchive.addValue("400");
    populationSizeWithArchive.setParentTag("externalArchive");

    algorithmResult.addAssociatedParameter(populationSize);
    algorithmResult.addAssociatedParameter(populationSizeWithArchive);
    parameters.add(algorithmResult);

    OrdinalParameterType offspringPopulationSize =
        new OrdinalParameterType("offspringPopulationSize");
    offspringPopulationSize.addValue("1");
    offspringPopulationSize.addValue("5");
    offspringPopulationSize.addValue("10");
    offspringPopulationSize.addValue("20");
    offspringPopulationSize.addValue("50");
    offspringPopulationSize.addValue("100");
    offspringPopulationSize.addValue("200");
    offspringPopulationSize.addValue("400");

    CategoricalParameterType variation = new CategoricalParameterType("variation");
    variation.addValue("rankingAndCrowding");

    CategoricalParameterType createInitialSolutions =
        new CategoricalParameterType("createInitialSolutions");
    createInitialSolutions.addValue("random");
    createInitialSolutions.addValue("scatterSearch");
    createInitialSolutions.addValue("latinHypercubeSampling");

    parameters.add(offspringPopulationSize);
    parameters.add(variation);
    parameters.add(createInitialSolutions);

    /* Crossover */
    CrossoverParameter crossover = new CrossoverParameter();
    crossover.addAssociatedParameter(new SBXCrossoverDistributionIndexParameter(5.0, 400.0));
    crossover.addAssociatedParameter(new BLXAlphaCrossoverAlphaValueParameter());

    parameters.add(crossover);

    /* Mutation */
    MutationParameter mutation = new MutationParameter();
    mutation.addAssociatedParameter(new PolynomialMutationDistributionIndexParameter(5.0, 200.0));
    mutation.addAssociatedParameter(new UniformMutationPerturbationParameter(0, 1));

    parameters.add(mutation);

    /* Selection */
    SelectionParameter selection = new SelectionParameter();
    selection.addAssociatedParameter(new NarityTournamentNParameter(2, 10));
    selection.addValue("random");

    parameters.add(selection);

    String formatString = "%-40s %-40s %-7s %-30s %-20s\n";
    StringBuilder stringBuilder = new StringBuilder();
    for (ParameterType parameter : parameters) {
      stringBuilder.append(
          String.format(
              formatString,
              parameter.getName(),
              parameter.getLabel(),
              parameter.getParameterType(),
              parameter.getRange(),
              parameter.getConditions()));

      for (ParameterType relatedParameter : parameter.getGlobalParameters()) {
        stringBuilder.append(
            String.format(
                formatString,
                relatedParameter.getName(),
                relatedParameter.getLabel(),
                relatedParameter.getParameterType(),
                relatedParameter.getRange(),
                relatedParameter.getConditions()));
      }
      for (ParameterType relatedParameter : parameter.getAssociatedParameters()) {
        stringBuilder.append(
            String.format(
                formatString,
                relatedParameter.getName(),
                relatedParameter.getLabel(),
                relatedParameter.getParameterType(),
                relatedParameter.getRange(),
                relatedParameter.getConditions()));
      }
      stringBuilder.append("#\n");
    }
    stringBuilder.append("\n");
    System.out.println(stringBuilder.toString());
    Files.write(Paths.get("parameters-NSGAII.txt"), stringBuilder.toString().getBytes());
  }
}
