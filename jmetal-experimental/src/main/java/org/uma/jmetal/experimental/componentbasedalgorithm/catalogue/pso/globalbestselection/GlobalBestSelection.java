package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestselection;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;

public interface GlobalBestSelection {
  DoubleSolution select(BoundedArchive<DoubleSolution> globalBestArchive) ;
}
