package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestselection.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestselection.GlobalBestSelection;
import org.uma.jmetal.operator.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;

public class TournamentGlobalBestSelection implements GlobalBestSelection {

  private int tournamentSize;

  public TournamentGlobalBestSelection(int tournamentSize) {
    this.tournamentSize = tournamentSize;
  }

  @Override
  public DoubleSolution select(BoundedArchive<DoubleSolution> globalBestArchive) {
    NaryTournamentSelection<DoubleSolution> tournament = new NaryTournamentSelection<>(
        tournamentSize, globalBestArchive.getComparator());

    return tournament.execute(globalBestArchive.getSolutionList());
  }
}
