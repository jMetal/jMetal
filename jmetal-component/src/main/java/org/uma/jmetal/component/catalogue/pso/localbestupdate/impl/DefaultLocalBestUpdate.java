package org.uma.jmetal.component.catalogue.pso.localbestupdate.impl;

import java.util.List;
import org.uma.jmetal.component.catalogue.pso.localbestupdate.LocalBestUpdate;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.comparator.dominanceComparator.DominanceComparator;

public class DefaultLocalBestUpdate implements LocalBestUpdate {
  private DominanceComparator<DoubleSolution> dominanceComparator ;

  /**
   * TODO: Terminar documentacion
   * Constructor
   * @param dominanceComparator
   */
  public DefaultLocalBestUpdate(DominanceComparator<DoubleSolution> dominanceComparator) {
    this.dominanceComparator = dominanceComparator ;
  }

  @Override
  public DoubleSolution[] update(List<DoubleSolution> swarm, DoubleSolution[] localBest) {
    for (int i = 0; i < swarm.size(); i++) {
      int result = dominanceComparator.compare(swarm.get(i), localBest[i]) ;
      if (result != 1) {
        localBest[i] = (DoubleSolution)swarm.get(i).copy() ;
      }
    }
    return localBest ;
  }

  public DominanceComparator<DoubleSolution> getDominanceComparator() {
    return dominanceComparator;
  }
}
