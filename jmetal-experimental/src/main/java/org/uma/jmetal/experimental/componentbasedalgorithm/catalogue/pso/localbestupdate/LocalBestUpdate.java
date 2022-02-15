package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestupdate;

import java.util.List;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public interface LocalBestUpdate {
  DoubleSolution[] update(List<DoubleSolution> swarm, DoubleSolution[] localBest) ;
}
