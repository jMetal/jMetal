package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization.GlobalBestInitialization;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;

import java.util.List;

/**
 * @author Antonio J. Nebro
 * @authro Daniel Doblas
 */
public class DefaultGlobalBestInitialization implements GlobalBestInitialization {
  @Override
  public BoundedArchive<DoubleSolution> initialize(List<DoubleSolution> swarm, BoundedArchive<DoubleSolution> globalBest) {
    swarm.forEach(globalBest::add);

    return globalBest;
  }
}
