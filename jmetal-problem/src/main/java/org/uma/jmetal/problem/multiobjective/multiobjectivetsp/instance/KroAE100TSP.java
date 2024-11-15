package org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance;

import java.io.IOException;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.BiObjectiveTSP;

public class KroAE100TSP extends BiObjectiveTSP {

  /**
   * Creates a new MultiobjectiveTSP problem instance
   *
   */
  public KroAE100TSP() throws IOException {
    super("resources/tspInstances/kroA100.tsp", "resources/tspInstances/kroE100.tsp");
  }

  @Override
  public String name() {
    return "KroAE100TSP" ;
  }
}
