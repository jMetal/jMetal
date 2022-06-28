package org.uma.jmetal.component.catalogue.pso.globalbestselection.impl;

import java.util.List;
import org.uma.jmetal.component.catalogue.pso.globalbestselection.GlobalBestSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class RandomGlobalBestSelection implements GlobalBestSelection {

  @Override
  public DoubleSolution select(List<DoubleSolution> globalBestList) {
    int randomSolution = JMetalRandom.getInstance()
        .nextInt(0, globalBestList.size() - 1);
    return globalBestList.get(randomSolution);
  }
}
