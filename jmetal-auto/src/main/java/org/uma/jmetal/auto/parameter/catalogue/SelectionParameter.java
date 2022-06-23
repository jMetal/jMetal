package org.uma.jmetal.auto.parameter.catalogue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.auto.component.catalogue.ea.selection.MatingPoolSelection;
import org.uma.jmetal.auto.component.catalogue.ea.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.auto.component.catalogue.ea.selection.impl.RandomMatingPoolSelection;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

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
