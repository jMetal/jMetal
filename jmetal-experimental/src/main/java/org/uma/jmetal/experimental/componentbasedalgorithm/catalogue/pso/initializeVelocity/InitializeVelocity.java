package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.initializeVelocity;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;

public interface InitializeVelocity {
  double[][] initialize(List<DoubleSolution> swarm) ;
}
