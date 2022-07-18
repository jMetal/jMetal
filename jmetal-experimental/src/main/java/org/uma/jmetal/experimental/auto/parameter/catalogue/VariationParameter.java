package org.uma.jmetal.experimental.auto.parameter.catalogue;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.variation.Variation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class VariationParameter extends CategoricalParameter {
  public VariationParameter(String[] args, List<String> variationStrategies) {
    super("variation", args, variationStrategies);
  }

  public @NotNull Variation<?> getParameter() {
    Variation<?> result;
    int offspringPopulationSize = (Integer)findGlobalParameter("offspringPopulationSize").getValue() ;

    switch (getValue()) {
      case "crossoverAndMutationVariation":
        var crossoverParameter =
            (CrossoverParameter) findSpecificParameter("crossover");
        var mutationParameter = (MutationParameter) findSpecificParameter("mutation");

        @NotNull CrossoverOperator<DoubleSolution> crossoverOperator = crossoverParameter.getParameter();
        @NotNull MutationOperator<DoubleSolution> mutationOperatorOperator =
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
