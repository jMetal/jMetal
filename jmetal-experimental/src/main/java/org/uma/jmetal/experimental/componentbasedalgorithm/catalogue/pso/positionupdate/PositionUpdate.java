package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.positionupdate;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;

import java.util.List;

public interface PositionUpdate {
  List<DoubleSolution> update(List<DoubleSolution> swarm, double[][]speed) ;
}
