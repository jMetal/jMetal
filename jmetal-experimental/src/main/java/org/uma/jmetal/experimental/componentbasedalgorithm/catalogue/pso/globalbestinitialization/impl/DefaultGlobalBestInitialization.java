package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization.GlobalBestInitialization;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.List;

/**
 * @author Antonio J. Nebro
 * @author Daniel Doblas
 */
public class DefaultGlobalBestInitialization implements GlobalBestInitialization {
  @Override
  /**
   * Initialize the Global Best solution.
   * @param: swarm: List of possibles solutions
   * @param: globalBest: List or Empty List of auxiliar solutions
   * @return: globalBest: List with differents global solutions.
   */
  public BoundedArchive<DoubleSolution> initialize(List<DoubleSolution> swarm, BoundedArchive<DoubleSolution> globalBest) {
    Check.notNull(swarm);
    Check.notNull(globalBest);
    Check.that(swarm.size() > 0, "The swarm size is empty: " + swarm.size());

    for (DoubleSolution doubleSolution : swarm) {
      globalBest.add(doubleSolution);
    }

    return globalBest;
  }
}
