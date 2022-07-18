package org.uma.jmetal.component.catalogue.pso.localbestinitialization.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.catalogue.pso.localbestinitialization.LocalBestInitialization;
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

    List<DoubleSolution> list = new ArrayList<>();
    for (DoubleSolution doubleSolution : swarm) {
      DoubleSolution copy = (DoubleSolution) doubleSolution.copy();
      list.add(copy);
    }
    DoubleSolution[] localBest = list.toArray(new DoubleSolution[0]);

      return localBest ;
  }
}
