package org.uma.jmetal.auto.component.catalogue.pso.globalbestupdate.impl;

import java.util.List;
import org.uma.jmetal.auto.component.catalogue.pso.globalbestupdate.GlobalBestUpdate;
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
  public BoundedArchive<DoubleSolution> update(List<DoubleSolution> swarm, BoundedArchive<DoubleSolution> globalBest) {
    Check.notNull(swarm);
    Check.notNull(globalBest);
    Check.that(swarm.size() > 0, "The swarm size is empty: " + swarm.size());

    swarm.stream().map(particle -> (DoubleSolution) particle.copy()).forEach(globalBest::add);
    return globalBest;
  }
}
