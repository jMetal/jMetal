package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityupdate;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;

import java.util.List;

public interface VelocityUpdate {
  double[][] update(List<DoubleSolution> swarm, double[][] speed, DoubleSolution[] localBest, BoundedArchive<DoubleSolution> globalBest) ;
}
