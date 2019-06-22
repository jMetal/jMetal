package org.uma.jmetal.auto.parameter.catalogue;

import org.uma.jmetal.auto.component.selection.MatingPoolSelection;
import org.uma.jmetal.auto.component.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.auto.component.selection.impl.RandomMatingPoolSelection;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

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
            pair -> {
              if (pair.getKey().equals(getValue())) {
                pair.getValue().parse().check();
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
