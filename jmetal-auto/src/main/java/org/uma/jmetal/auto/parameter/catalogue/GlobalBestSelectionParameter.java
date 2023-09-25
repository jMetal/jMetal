package org.uma.jmetal.auto.parameter.catalogue;

import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.pso.globalbestselection.GlobalBestSelection;
import org.uma.jmetal.component.catalogue.pso.globalbestselection.impl.NaryTournamentGlobalBestSelection;
import org.uma.jmetal.component.catalogue.pso.globalbestselection.impl.RandomGlobalBestSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class GlobalBestSelectionParameter extends CategoricalParameter {
  public GlobalBestSelectionParameter(List<String> selectionStrategies) {
    super("globalBestSelection", selectionStrategies) ;
  }

  public GlobalBestSelection getParameter(Comparator<DoubleSolution> comparator) {
    GlobalBestSelection result ;
    switch(value()) {
      case "tournament":
        int tournamentSize =
            (Integer) findSpecificParameter("selectionTournamentSize").value();

        result = new NaryTournamentGlobalBestSelection(tournamentSize, comparator) ;
        break ;
      case "random":
        result = new RandomGlobalBestSelection();
        break ;
      default:
        throw new JMetalException("Global Best Selection component unknown: " + value()) ;
    }

    return result ;
  }
}
