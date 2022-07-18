package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestupdate.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestupdate.GlobalBestUpdate;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * @author Antonio J. Nebro
 * @author Daniel Doblas
 */
public class DefaultGlobalBestUpdate implements GlobalBestUpdate {
  @Override
  /**
   * Update the List of best solutions of the Global Best's List
   * @param swarm: List of possibles solutions
   * @param globalBest: List or Empty List of auxiliar solutions
   * @return List of global best solutions
   */
  public @NotNull BoundedArchive<DoubleSolution> update(List<DoubleSolution> swarm, BoundedArchive<DoubleSolution> globalBest) {
    Check.notNull(swarm);
    Check.notNull(globalBest);
    Check.that(swarm.size() > 0, "The swarm size is empty: " + swarm.size());

    for (var particle : swarm) {
      @Nullable DoubleSolution copy = (DoubleSolution) particle.copy();
      globalBest.add(copy);
    }
    return globalBest;
  }
}
