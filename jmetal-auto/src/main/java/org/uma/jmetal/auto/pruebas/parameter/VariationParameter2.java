package org.uma.jmetal.auto.pruebas.parameter;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.auto.pruebas.operator.Crossover2;
import org.uma.jmetal.auto.pruebas.operator.Mutation2;
import org.uma.jmetal.auto.pruebas.operator.PolynomialMutation2;
import org.uma.jmetal.auto.pruebas.operator.SBXCrossover2;
import org.uma.jmetal.auto.pruebas.solution.DoubleSolution2;
import org.uma.jmetal.auto.pruebas.variation.CrossoverAndMutationVariation2;
import org.uma.jmetal.auto.pruebas.variation.Variation2;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class VariationParameter2 extends CategoricalParameter {
  public VariationParameter2(String[] args, List<String> variationStrategies) {
    super("variation", args, variationStrategies);
  }

  public Variation2<? extends DoubleSolution2> getDoubleSolutionParameter() {
    Variation2<? extends DoubleSolution2> result;
    int offspringPopulationSize = (Integer)findGlobalParameter("offspringPopulationSize").getValue() ;

    if ("crossoverAndMutationVariation".equals(getValue())) {
      Crossover2<DoubleSolution2> crossover2 = new SBXCrossover2(0.1, 4.0) ;
      Mutation2 mutation2 = new PolynomialMutation2(0.1, 1.0, () -> JMetalRandom.getInstance().nextDouble()) ;

      result =
          new CrossoverAndMutationVariation2<>(
              offspringPopulationSize, crossover2, mutation2);
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
