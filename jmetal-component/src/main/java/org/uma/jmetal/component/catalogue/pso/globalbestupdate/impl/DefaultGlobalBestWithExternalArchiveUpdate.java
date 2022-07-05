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
public class DefaultGlobalBestWithExternalArchiveUpdate extends DefaultGlobalBestUpdate {

  private Archive<DoubleSolution> externalArchive;

  public DefaultGlobalBestWithExternalArchiveUpdate(Archive<DoubleSolution> externalArchive) {
    this.externalArchive = externalArchive;
  }

  public BoundedArchive<DoubleSolution> update(List<DoubleSolution> swarm,
      BoundedArchive<DoubleSolution> globalBest) {
    Check.notNull(externalArchive);
    swarm.stream().map(particle -> (DoubleSolution) particle.copy()).forEach(externalArchive::add);

    return super.update(swarm, globalBest);
  }
}