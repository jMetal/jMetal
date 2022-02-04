package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization.GlobalBestInitialization;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.List;

/**
 * @author Antonio J. Nebro
 * @authro Daniel Doblas
 */
public class DefaultGlobalBestInitialization implements GlobalBestInitialization {
  @Override
  public BoundedArchive<DoubleSolution> initialize(List<DoubleSolution> swarm, BoundedArchive<DoubleSolution> globalBest) {
    Check.notNull(swarm);
    Check.that(swarm.size() > 0, "The swarm size is empty: " + swarm.size());

    swarm.forEach(globalBest::add);

    return globalBest;
  }
}
