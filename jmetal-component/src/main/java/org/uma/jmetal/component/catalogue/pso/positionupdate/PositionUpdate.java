package org.uma.jmetal.component.catalogue.pso.positionupdate;

import java.util.List;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public interface PositionUpdate {
  List<DoubleSolution> update(List<DoubleSolution> swarm, double[][]speed) ;
}
