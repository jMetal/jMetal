package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.initializelocalbest;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;

public interface LocalBestInitialization {
  DoubleSolution[] initialize(List<DoubleSolution> swarm) ;
}
