package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestselection.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestselection.GlobalBestSelection;
import org.uma.jmetal.operator.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class TournamentGlobalBestSelection implements GlobalBestSelection {

  private int tournamentSize;

  public TournamentGlobalBestSelection(int tournamentSize) {
    this.tournamentSize = tournamentSize;
  }

  @Override
  public DoubleSolution select(BoundedArchive<DoubleSolution> globalBestArchive) {
    /*
    NaryTournamentSelection<DoubleSolution> tournament = new NaryTournamentSelection<>(
        tournamentSize, globalBestArchive.getComparator().reversed());

    return tournament.execute(globalBestArchive.getSolutionList());
    */

    DoubleSolution one, two;
    DoubleSolution bestGlobal;
    int pos1 = JMetalRandom.getInstance().nextInt(0, globalBestArchive.getSolutionList().size() - 1);
    int pos2 = JMetalRandom.getInstance().nextInt(0, globalBestArchive.getSolutionList().size() - 1);
    one = globalBestArchive.getSolutionList().get(pos1);
    two = globalBestArchive.getSolutionList().get(pos2);

    if (globalBestArchive.getComparator().compare(one, two) < 1) {
      bestGlobal = (DoubleSolution) one.copy();
    } else {
      bestGlobal = (DoubleSolution) two.copy();
    }

    return bestGlobal;
  }
}
