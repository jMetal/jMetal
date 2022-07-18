package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestinitialization.impl;

import java.util.List;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestinitialization.LocalBestInitialization;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * @author Antonio J. Nebro
 * @author Daniel Doblas
 */
public class DefaultLocalBestInitialization implements LocalBestInitialization {
  /**
   * Initialize the local best solutions.
   * @param swarm: List of possible solutions
   * @return A list of the best local solutions.
   */
  public DoubleSolution[] initialize(List<DoubleSolution> swarm) {
    Check.notNull(swarm);
    Check.that(swarm.size() > 0, "The swarm size is empty: " + swarm.size());

    DoubleSolution[] localBest = swarm.stream().map(doubleSolution -> (DoubleSolution) doubleSolution.copy()).toArray(DoubleSolution[]::new);

      return localBest ;
  }
}
