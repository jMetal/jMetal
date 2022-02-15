package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestselection.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestselection.GlobalBestSelection;
import org.uma.jmetal.operator.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class RandomGlobalBestSelection implements GlobalBestSelection {

  @Override
  public DoubleSolution select(BoundedArchive<DoubleSolution> globalBestArchive) {
    int randomSolution = JMetalRandom.getInstance().nextInt(0, globalBestArchive.getSolutionList().size()) ;
    return globalBestArchive.getSolutionList().get(randomSolution) ;
  }
}
