package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityupdate;

import java.util.List;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestselection.GlobalBestSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;

public interface VelocityUpdate {

  double[][] update(List<DoubleSolution> swarm, double[][] speed, DoubleSolution[] localBest,
      BoundedArchive<DoubleSolution> globalBest, GlobalBestSelection globalBestSelection);
}
