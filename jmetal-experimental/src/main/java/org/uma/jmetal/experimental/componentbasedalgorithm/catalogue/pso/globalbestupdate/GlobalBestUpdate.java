package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestupdate;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;

import java.util.List;

public interface GlobalBestUpdate {
  BoundedArchive<DoubleSolution> update(List<DoubleSolution> swarm, BoundedArchive<DoubleSolution> globalBest) ;
}
