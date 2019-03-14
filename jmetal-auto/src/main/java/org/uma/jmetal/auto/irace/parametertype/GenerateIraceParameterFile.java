package org.uma.jmetal.auto.irace.parametertype;

import org.uma.jmetal.auto.irace.parametertype.impl.CategoricalParameterType;
import org.uma.jmetal.auto.irace.parametertype.impl.IntegerParameterType;
import org.uma.jmetal.auto.irace.parametertype.impl.RealParameterType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GenerateIraceParameterFile {
  public static void main(String[] args) throws IOException {
    List<ParameterType> parameters = new ArrayList<>();

    /* Crossover */
    RealParameterType crossoverProbability =
        new RealParameterType("crossoverProbability", "--crossoverProbability", 0, 1);

    RealParameterType sbxDistributionIndex = new RealParameterType("sbxCrossoverDistributionIndex", 5.0, 400.0) ;
    sbxDistributionIndex.setParentTag("SBX");

    RealParameterType blxAlphaCrossoverAlphaValue = new RealParameterType("blxAlphaCrossoverAlphaValue", 0.0, 1.0) ;
    blxAlphaCrossoverAlphaValue.setParentTag("BLX_ALPHA");

    CategoricalParameterType crossoverRepairStrategy = new CategoricalParameterType("crossoverRepairStrategy") ;
    crossoverRepairStrategy.addValue("random");
    crossoverRepairStrategy.addValue("bounds");
    crossoverRepairStrategy.addValue("round");

    CategoricalParameterType crossover = new CategoricalParameterType("crossover", "--crossover");

    crossover.addGlobalParameter(crossoverProbability);
    crossover.addGlobalParameter(crossoverRepairStrategy);
    crossover.addAssociatedParameter(sbxDistributionIndex);
    crossover.addAssociatedParameter(blxAlphaCrossoverAlphaValue);

    parameters.add(crossover);

    /* Mutation */
    RealParameterType mutationProbability = new RealParameterType("mutationProbability", 0, 1) ;

    RealParameterType polynomialMutationDistributionIndex = new RealParameterType("polynomialMutationDistributionIndex", 5.0, 400.0) ;
    polynomialMutationDistributionIndex.setParentTag("polynomial");

    RealParameterType uniformMutationPerturbation = new RealParameterType("uniformMutationPerturbation", 0.0, 1.0) ;
    uniformMutationPerturbation.setParentTag("uniform");

    CategoricalParameterType mutationRepairStrategy = new CategoricalParameterType("mutationRepairStrategy") ;
    mutationRepairStrategy.addValue("random");
    mutationRepairStrategy.addValue("bounds");
    mutationRepairStrategy.addValue("round");

    CategoricalParameterType mutation = new CategoricalParameterType("mutation") ;

    mutation.addGlobalParameter(mutationProbability);
    mutation.addGlobalParameter(mutationRepairStrategy);
    mutation.addAssociatedParameter(polynomialMutationDistributionIndex);
    mutation.addAssociatedParameter(uniformMutationPerturbation);

    parameters.add(mutation) ;

    /* Selection */
    IntegerParameterType nArityTournament = new IntegerParameterType("selectionTournamentSize", 1, 10) ;
    nArityTournament.setParentTag("tournament");

    CategoricalParameterType selection = new CategoricalParameterType("selection") ;
    selection.addAssociatedParameter(nArityTournament);
    selection.addValue("random");

    parameters.add(selection) ;

    /* Other */
    IntegerParameterType offspringPopulationSize = new IntegerParameterType("offspringPopulationSize", 1, 400) ;

    CategoricalParameterType variation = new CategoricalParameterType("variation") ;
    variation.addValue("rankingAndCrowding");

    CategoricalParameterType createInitialSolutions = new CategoricalParameterType("createInitialSolutions") ;
    createInitialSolutions.addValue("random");

    parameters.add(offspringPopulationSize) ;
    parameters.add(variation) ;
    parameters.add(createInitialSolutions) ;


    String formatString = "%-40s %-40s %-7s %-30s %-20s\n";
    StringBuilder stringBuilder = new StringBuilder() ;
    for (ParameterType parameter : parameters) {
      stringBuilder.append(String.format(
          formatString,
          parameter.getName(),
          parameter.getLabel(),
          parameter.getParameterType(),
          parameter.getRange(),
          parameter.getConditions()));

      for (ParameterType relatedParameter : parameter.getGlobalParameters()) {
        stringBuilder.append(String.format(
            formatString,
            relatedParameter.getName(),
            relatedParameter.getLabel(),
            relatedParameter.getParameterType(),
            relatedParameter.getRange(),
            relatedParameter.getConditions()));
      }
      for (ParameterType relatedParameter : parameter.getAssociatedParameters()) {
        stringBuilder.append(String.format(
            formatString,
            relatedParameter.getName(),
            relatedParameter.getLabel(),
            relatedParameter.getParameterType(),
            relatedParameter.getRange(),
            relatedParameter.getConditions()));
      }
    }
    stringBuilder.append("\n") ;
    System.out.println(stringBuilder.toString()) ;
    Files.write(Paths.get("parameters-NSGAII.txt"), stringBuilder.toString().getBytes());
  }
}
