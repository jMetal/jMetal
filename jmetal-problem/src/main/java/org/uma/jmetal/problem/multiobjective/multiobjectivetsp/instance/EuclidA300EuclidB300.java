package org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance;

import java.io.IOException;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.MultiObjectiveTSP;

public class EuclidA300EuclidB300 extends MultiObjectiveTSP {

  /**
   * Creates a new MultiobjectiveTSP problem instance
   *
   */
  public EuclidA300EuclidB300() throws IOException {
    super("resources/tspInstances/euclidA300.tsp", "resources/tspInstances/euclidB300.tsp");
  }

  @Override
  public String name() {
    return "EuclidA300EuclidB300" ;
  }
}
