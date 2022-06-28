package org.uma.jmetal.auto.parameter.catalogue;

import java.util.Comparator;
import java.util.List;
import component.catalogue.ea.selection.MatingPoolSelection;
import component.catalogue.ea.selection.impl.NaryTournamentMatingPoolSelection;
import component.catalogue.ea.selection.impl.RandomMatingPoolSelection;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class SelectionParameter<S extends Solution<?>> extends CategoricalParameter {
  public SelectionParameter(String args[], List<String> selectionStrategies) {
    super("selection", args, selectionStrategies) ;
  }

  public MatingPoolSelection<S> getParameter(int matingPoolSize, Comparator<S> comparator) {
    MatingPoolSelection<S> result ;
    switch(getValue()) {
      case "tournament":
        int tournamentSize =
                (Integer) findSpecificParameter("selectionTournamentSize").getValue();

        result = new NaryTournamentMatingPoolSelection<S>(
                tournamentSize, matingPoolSize, comparator);

        break ;
      case "random":
        result = new RandomMatingPoolSelection<>(matingPoolSize);
        break ;
      default:
        throw new JMetalException("Selection component unknown: " + getValue()) ;
    }

    return result ;
  }
}
