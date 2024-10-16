package org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance;

import java.io.IOException;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.BiObjectiveTSP;

public class EuclidAB300 extends BiObjectiveTSP {

  /**
   * Creates a new MultiobjectiveTSP problem instance
   *
   */
  public EuclidAB300() throws IOException {
    super("resources/tspInstances/euclidA300.tsp", "resources/tspInstances/euclidB300.tsp");
  }

  @Override
  public String name() {
    return "EuclidAB300" ;
  }
}
