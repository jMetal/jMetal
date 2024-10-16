package org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance;

import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.BiObjectiveTSP;

import java.io.IOException;

public class KroAB100TSP extends BiObjectiveTSP {

  /**
   * Creates a new MultiobjectiveTSP problem instance
   *
   */
  public KroAB100TSP() throws IOException {
    super("resources/tspInstances/kroA100.tsp", "resources/tspInstances/kroB100.tsp");
  }

  @Override
  public String name() {
    return "KroAB100TSP" ;
  }
}
