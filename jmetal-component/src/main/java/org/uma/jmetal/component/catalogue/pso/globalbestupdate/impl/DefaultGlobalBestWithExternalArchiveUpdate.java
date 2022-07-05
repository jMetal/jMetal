package org.uma.jmetal.component.catalogue.pso.globalbestupdate.impl;

import java.util.List;
import org.uma.jmetal.component.catalogue.pso.globalbestupdate.GlobalBestUpdate;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;
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
   * @param globalBest: List or Empty List of auxiliary solutions
   * @return List of global best solutions
   */
  public BoundedArchive<DoubleSolution> update(List<DoubleSolution> swarm,
      BoundedArchive<DoubleSolution> globalBest) {
    Check.notNull(swarm);
    Check.notNull(globalBest);
    Check.that(!swarm.isEmpty(), "The swarm size is empty: " + swarm.size());

    swarm.stream().map(particle -> (DoubleSolution) particle.copy()).forEach(globalBest::add);
    return globalBest;
  }

  public BoundedArchive<DoubleSolution> update(List<DoubleSolution> swarm,
      BoundedArchive<DoubleSolution> globalBest, Archive<DoubleSolution> externalArchive) {
    Check.notNull(externalArchive);
    swarm.stream().map(particle -> (DoubleSolution) particle.copy()).forEach(externalArchive::add);

    return this.update(swarm, globalBest);
  }
}