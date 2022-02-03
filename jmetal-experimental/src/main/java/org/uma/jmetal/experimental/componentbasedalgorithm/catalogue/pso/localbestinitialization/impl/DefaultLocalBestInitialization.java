package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestinitialization.impl ;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestinitialization.LocalBestInitialization;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.List;

/**
 * TODO: comment the class
 *
 * @author Antonio J. Nebro
 * @author Daniel Doblas
 */
public class DefaultLocalBestInitialization implements LocalBestInitialization {
  public DoubleSolution[] initialize(List<DoubleSolution> swarm) {
    Check.notNull(swarm);

    DoubleSolution[] localBest = new DoubleSolution[swarm.size()] ;
    for (int i = 0; i < swarm.size(); i++) {
      localBest[i] = (DoubleSolution) swarm.get(i).copy() ;
    }

    return localBest ;
  }
}
