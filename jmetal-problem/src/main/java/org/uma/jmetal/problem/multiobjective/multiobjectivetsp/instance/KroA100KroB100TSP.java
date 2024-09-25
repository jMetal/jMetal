package org.uma.jmetal.problem.multiobjective.multiobjectivetsp.instance;

import org.uma.jmetal.problem.multiobjective.multiobjectivetsp.MultiObjectiveTSP;

import java.io.IOException;

public class KroA100KroB100TSP extends MultiObjectiveTSP {

  /**
   * Creates a new MultiobjectiveTSP problem instance
   *
   */
  public KroA100KroB100TSP() throws IOException {
    super("resources/tspInstances/kroA100.tsp", "resources/tspInstances/kroB100.tsp");
  }

  @Override
  public String name() {
    return "KroA100KroB100TSP" ;
  }
}
