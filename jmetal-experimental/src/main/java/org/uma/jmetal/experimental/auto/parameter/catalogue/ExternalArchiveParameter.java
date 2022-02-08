package org.uma.jmetal.experimental.auto.parameter.catalogue;

import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.variation.Variation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;

public class ExternalArchiveParameter extends CategoricalParameter {
  public ExternalArchiveParameter(String[] args, List<String> externalArchiveTypes) {
    super("variation", args, externalArchiveTypes);
  }

  public Variation<?> getParameter() {
    Variation<?> result;
    int offspringPopulationSize = (Integer)findGlobalParameter("offspringPopulationSize").getValue() ;

    switch (getValue()) {
      case "crowdingDistanceArchive":
        CrossoverParameter crossoverParameter =
            (CrossoverParameter) findSpecificParameter("crossover");
        MutationParameter mutationParameter = (MutationParameter) findSpecificParameter("mutation");

        CrossoverOperator<DoubleSolution> crossoverOperator = crossoverParameter.getParameter();
        MutationOperator<DoubleSolution> mutationOperatorOperator =
            mutationParameter.getParameter();

        result =
            new CrossoverAndMutationVariation<>(
                offspringPopulationSize, crossoverOperator, mutationOperatorOperator);
        break;
      default:
        throw new RuntimeException("Variation component unknown: " + getValue());
    }

    return result;
  }

  @Override
  public String getName() {
    return "variation";
  }
}
