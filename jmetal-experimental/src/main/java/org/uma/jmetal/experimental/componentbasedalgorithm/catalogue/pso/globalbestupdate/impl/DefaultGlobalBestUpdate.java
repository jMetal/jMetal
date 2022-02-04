package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestupdate.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestupdate.GlobalBestUpdate;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;

import java.util.List;

public class DefaultGlobalBestUpdate implements GlobalBestUpdate {
  @Override
  public BoundedArchive<DoubleSolution> update(List<DoubleSolution> swarm, BoundedArchive<DoubleSolution> globalBest) {
    swarm.stream().map(particle -> (DoubleSolution) particle.copy()).forEach(globalBest::add);
    return globalBest;
  }
}
