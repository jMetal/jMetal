package org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance;

import java.io.IOException;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.BiObjectiveTSP;

public class KroBC100TSP extends BiObjectiveTSP {

  /**
   * Creates a new MultiobjectiveTSP problem instance
   *
   */
  public KroBC100TSP() throws IOException {
    super("resources/tspInstances/kroB100.tsp", "resources/tspInstances/kroAC100.tsp");
  }

  @Override
  public String name() {
    return "KroBC100TSP" ;
  }
}
