package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.component.selection.MatingPoolSelection;
import org.uma.jmetal.auto.component.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.auto.component.selection.impl.RandomMatingPoolSelection;
import org.uma.jmetal.auto.component.variation.Variation;
import org.uma.jmetal.auto.component.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class SelectionParameter extends CategoricalParameter<String> {
  public SelectionParameter(String args[], List<String> selectionStrategies) {
    super("selection", args, selectionStrategies) ;
  }

  public CategoricalParameter<String> parse() {
    setValue(on("--selection", getArgs(), Function.identity()));

    getSpecificParameters()
        .forEach(
            (key, parameter) -> {
              if (key.equals(getValue())) {
                parameter.parse().check();
              }
            });

    return this;
  }

  public MatingPoolSelection<?> getParameter(int matingPoolSize, Comparator<DoubleSolution> comparator) {
    MatingPoolSelection<?> result ;
    switch(getValue()) {
      case "tournament":
        int tournamentSize =
                (Integer) findSpecificParameter("selectionTournamentSize").getValue();
        result = new NaryTournamentMatingPoolSelection<>(
                tournamentSize, matingPoolSize, comparator);

        break ;
      case "random":
        result = new RandomMatingPoolSelection<>(matingPoolSize);
        break ;
      default:
        throw new RuntimeException("Selection component unknown: " + getValue()) ;
    }

    return result ;
  }
}
