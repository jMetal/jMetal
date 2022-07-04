package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class VariationParameter extends CategoricalParameter {
  public VariationParameter(String[] args, List<String> variationStrategies) {
    super("variation", args, variationStrategies);
  }

  public Variation<DoubleSolution> getParameter() {
    Variation<DoubleSolution> result;
    int offspringPopulationSize = (Integer)findGlobalParameter("offspringPopulationSize").getValue() ;

    if ("crossoverAndMutationVariation".equals(getValue())) {
      CrossoverParameter crossoverParameter =
          (CrossoverParameter) findSpecificParameter("crossover");
      MutationParameter mutationParameter = (MutationParameter) findSpecificParameter("mutation");

      CrossoverOperator<DoubleSolution> crossoverOperator = crossoverParameter.getParameter();
      MutationOperator<DoubleSolution> mutationOperatorOperator =
          mutationParameter.getParameter();

      result =
          new CrossoverAndMutationVariation<>(
              offspringPopulationSize, crossoverOperator, mutationOperatorOperator);
    } else {
      throw new JMetalException("Variation component unknown: " + getValue());
    }

    return result;
  }

  @Override
  public String getName() {
    return "variation";
  }
}
