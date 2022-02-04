package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestupdate;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;

public interface LocalBestUpdate {
  DoubleSolution[] update(List<DoubleSolution> swarm, DoubleSolution[] localBest) ;
}
