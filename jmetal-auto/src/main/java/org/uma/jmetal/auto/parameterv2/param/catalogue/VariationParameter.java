package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.component.selection.MatingPoolSelection;
import org.uma.jmetal.auto.component.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.auto.component.selection.impl.RandomMatingPoolSelection;
import org.uma.jmetal.auto.component.variation.Variation;
import org.uma.jmetal.auto.component.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;
import org.uma.jmetal.auto.parameterv2.param.Parameter;
import org.uma.jmetal.operator.Operator;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import weka.Run;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class VariationParameter extends CategoricalParameter<String> {
  public VariationParameter(String args[], List<String> variationStrategies) {
    super("variation", args, variationStrategies) ;
  }

  public CategoricalParameter<String> parse() {
    setValue(on("--variation", getArgs(), Function.identity()));

    for (Parameter<?> parameter : getGlobalParameters()) {
      parameter.parse().check();
    }

    return this ;
  }

  public Variation<?> getParameter(int offspringPopulationSize) {
    Variation<?> result ;
    switch(getValue()) {
      case "crossoverAndMutationVariation":
        CrossoverParameter crossoverParameter = (CrossoverParameter)findGlobalParameter("crossover") ;
        MutationParameter mutationParameter = (MutationParameter) findGlobalParameter("mutation") ;

        CrossoverOperator<DoubleSolution> crossoverOperator = crossoverParameter.getParameter() ;
        MutationOperator<DoubleSolution> mutationOperatorOperator = mutationParameter.getParameter() ;

        result = new CrossoverAndMutationVariation<DoubleSolution>(offspringPopulationSize, crossoverOperator, mutationOperatorOperator) ;
        break ;
      default:
        throw new RuntimeException("Variation component unknown: " + getValue()) ;
    }

    return result ;
  }

  @Override
  public String getName() {
    return "variation";
  }
}
