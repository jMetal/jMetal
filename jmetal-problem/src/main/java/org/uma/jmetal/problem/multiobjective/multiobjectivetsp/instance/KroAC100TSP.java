package org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance;

import java.io.IOException;
import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.BiObjectiveTSP;

public class KroAC100TSP extends BiObjectiveTSP {

  /**
   * Creates a new MultiobjectiveTSP problem instance
   *
   */
  public KroAC100TSP() throws IOException {
    super("resources/tspInstances/kroA100.tsp", "resources/tspInstances/kroC100.tsp");
  }

  @Override
  public String name() {
    return "KroAC100TSP" ;
  }
}
