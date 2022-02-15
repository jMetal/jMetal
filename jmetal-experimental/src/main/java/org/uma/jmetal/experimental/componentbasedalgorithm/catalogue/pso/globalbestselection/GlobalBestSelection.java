package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestselection;

import java.util.List;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public interface GlobalBestSelection {
  DoubleSolution select(List<DoubleSolution> globalBestList) ;
}
