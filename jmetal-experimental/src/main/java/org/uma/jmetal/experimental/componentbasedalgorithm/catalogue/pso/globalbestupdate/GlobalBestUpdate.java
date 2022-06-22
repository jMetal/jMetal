package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestupdate;

import java.util.List;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;

public interface GlobalBestUpdate {
  BoundedArchive<DoubleSolution> update(List<DoubleSolution> swarm, BoundedArchive<DoubleSolution> globalBest) ;
}
