package org.uma.jmetal.auto.parameter.catalogue;

import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.PopulationAndNeighborhoodSelection;
import org.uma.jmetal.component.catalogue.ea.selection.impl.RandomSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

public class SelectionParameter<S extends Solution<?>> extends CategoricalParameter {

  public SelectionParameter(String args[], List<String> selectionStrategies) {
    super("selection", args, selectionStrategies);
  }

  public Selection<S> getParameter(int matingPoolSize, Comparator<S> comparator) {
    Selection<S> result;
    switch (getValue()) {
      case "tournament":
        int tournamentSize =
            (Integer) findSpecificParameter("selectionTournamentSize").getValue();

        result = new NaryTournamentSelection<>(
            tournamentSize, matingPoolSize, comparator);

        break;
      case "random":
        result = new RandomSelection<>(matingPoolSize);
        break;
      case "populationAndNeighborhoodMatingPoolSelection":
        double neighborhoodSelectionProbability =
            (double) findSpecificParameter("neighborhoodSelectionProbability").getValue();
        var neighborhood = (Neighborhood<S>) getNonConfigurableParameter("neighborhood");
        Check.notNull(neighborhood);

        var subProblemIdGenerator = (SequenceGenerator<Integer>) getNonConfigurableParameter(
            "subProblemIdGenerator");
        Check.notNull(subProblemIdGenerator);

        result =
            new PopulationAndNeighborhoodSelection<>(
                matingPoolSize,
                subProblemIdGenerator,
                neighborhood,
                neighborhoodSelectionProbability,
                false);
        break;
      default:
        throw new JMetalException("Selection component unknown: " + getValue());
    }

    return result;
  }
}
