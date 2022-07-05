package org.uma.jmetal.component.catalogue.pso.globalbestinitialization.impl;

import java.util.List;
import org.uma.jmetal.component.catalogue.pso.globalbestinitialization.GlobalBestInitialization;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * @author Antonio J. Nebro
 * @author Daniel Doblas
 */
public class DefaultGlobalBestWithExternalArchiveInitialization extends DefaultGlobalBestInitialization {
  private Archive<DoubleSolution> externalArchive;

  public DefaultGlobalBestWithExternalArchiveInitialization(Archive<DoubleSolution> externalArchive) {
    this.externalArchive = externalArchive ;
  }
  public BoundedArchive<DoubleSolution> initialize(List<DoubleSolution> swarm,
      BoundedArchive<DoubleSolution> globalBest) {
    Check.notNull(externalArchive);
    swarm.stream().map(particle -> (DoubleSolution) particle.copy()).forEach(externalArchive::add);

    return super.initialize(swarm, globalBest) ;
  }
}
